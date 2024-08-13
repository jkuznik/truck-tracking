package pl.jkuznik.trucktracking.domain.truck;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.QTruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class TruckService implements TruckApi {

    private final Integer DEFAULT_PAGE_NUMBER = 0;
    private final Integer DEFAULT_PAGE_SIZE = 25;

    private final TruckRepository truckRepository;
    private final TrailerRepository trailerRepository;
    private final TTHRepository tthRepository;

    @PersistenceContext
    private EntityManager em;

    @Override
    public TruckDTO addTruck(AddTruckCommand newTruck) {

        Optional<Truck> byRegisterPlateNumber = truckRepository.findByRegisterPlateNumber(newTruck.registerPlateNumber());

        if (byRegisterPlateNumber.isPresent()) {
            throw new RuntimeException("Plate number already exists");
        }

        return convert(truckRepository.save(new Truck(
                UUID.randomUUID(),
                newTruck.registerPlateNumber())));
    }

    @Override
    public TruckDTO getTruckByBusinessId(UUID uuid) {
        return convert(truckRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " + uuid + " not found")));
    }

        @Override
    public Page<TruckDTO> getAllTrucks(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = getPageRequest(pageNumber, pageSize);

        return new PageImpl<>(truckRepository.findAll(pageRequest).stream()
                .map(this::convert)
                .toList());
    }

    @Override
    public Page<TruckDTO> getAllTrucksUsedInLastMonth(Integer pageNumber, Integer pageSize) {

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(now, zoneId);
        Instant montAgo = zdt.minusMonths(1).toInstant();
        List<TruckTrailerHistory> fetchedTTH = new ArrayList<>();

        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("TTHpersistence-unit")) {
            EntityManager em = emf.createEntityManager();
            JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, em);

            QTruckTrailerHistory qtth = QTruckTrailerHistory.truckTrailerHistory;

            List<TruckTrailerHistory> fetch = queryFactory.selectFrom(qtth)
                    .where(qtth.startPeriodDate.after(montAgo))
                    .where(qtth.endPeriodDate.after(montAgo))
                    .fetch();

            fetchedTTH.addAll(fetch);

        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        List<TruckDTO> list = fetchedTTH.stream()
                .map(TruckTrailerHistory::getTruck)
                .map(this::convert)
                .toList();

        return new PageImpl<>(list);

    }

    private PageRequest getPageRequest(Integer pageNumber, Integer pageSize) {
        int number;
        int size;

        if (pageNumber != null && pageNumber > 0) {
            number = pageNumber;
        } else {
            number = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize != null && pageSize > 0) {
            if (pageSize > 100) {
                size = 100;
            } else {
                size = pageSize;
            }
        } else {
            size = DEFAULT_PAGE_SIZE;
        }

        return PageRequest.of(number, size);
    }


    @Transactional
    @Override
    public TruckDTO updateTruckAssignByBusinessId(UUID uuid, UpdateTruckCommand updateTruckCommand) {
        Truck truck = truckRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " + uuid.toString() + " not found"));

        Trailer trailer;

        if (updateTruckCommand.trailerId().isPresent()) {
            trailer = trailerRepository.findByBusinessId(updateTruckCommand.trailerId().get())
                    .orElseThrow(() -> new NoSuchElementException("No trailer with id " + updateTruckCommand.trailerId().get()));
        } else {
            throw new NoSuchElementException("Trailer business id is needed in this operation");
        }

        try {
            trailer.isInUse(updateTruckCommand.startPeriod().orElse(null), updateTruckCommand.endPeriod().orElse(null));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (updateTruckCommand.startPeriod().isPresent() && updateTruckCommand.endPeriod().isPresent()) {
            if (updateTruckCommand.endPeriod().get().isBefore(updateTruckCommand.startPeriod().get())) {
                try {
                    throw new Exception("End period is before start period");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        truck.setStartPeriodDate(updateTruckCommand.startPeriod().orElse(null));
        truck.setEndPeriodDate(updateTruckCommand.endPeriod().orElse(null));
        if (updateTruckCommand.startPeriod().isEmpty() && updateTruckCommand.endPeriod().isEmpty()) {
            truck.setCurrentTrailerBusinessId(null);
        } else {
            truck.setCurrentTrailerBusinessId(trailer.getBusinessId());
        }

        trailer.setStartPeriodDate(updateTruckCommand.startPeriod().orElse(null));
        trailer.setEndPeriodDate(updateTruckCommand.endPeriod().orElse(null));
        if (updateTruckCommand.startPeriod().isEmpty() && updateTruckCommand.endPeriod().isEmpty()) {
            trailer.setCurrentTruckBusinessId(null);
        } else {
            trailer.setCurrentTruckBusinessId(truck.getBusinessId());
        }

        var tth = new TruckTrailerHistory();

        tth.setTrailer(trailer);
        tth.setTruck(truck);
        if (updateTruckCommand.startPeriod().isPresent()) tth.setStartPeriodDate(updateTruckCommand.startPeriod().get());
        if (updateTruckCommand.endPeriod().isPresent()) tth.setEndPeriodDate(updateTruckCommand.endPeriod().get());

        return convert(truck);
    }

    @Transactional
    @Override
    public void deleteTruckByBusinessId(UUID uuid) {
        Truck truck = truckRepository.findByBusinessId(uuid).orElseThrow(
                () -> new NoSuchElementException("Truck with business id " + uuid + " not found")
        );

        if (truck.getCurrentTrailerBusinessId() != null) {
            Optional<Trailer> trailer = trailerRepository.findByBusinessId(truck.getCurrentTrailerBusinessId());

            if (trailer.isPresent()) {
                trailer.get().setStartPeriodDate(null);
                trailer.get().setEndPeriodDate(null);
                trailer.get().setCurrentTruckBusinessId(null);

                trailerRepository.save(trailer.get());
            }
        }

        truckRepository.deleteByBusinessId(uuid);
    }

    private TruckDTO convert(Truck truck) {
        return new TruckDTO(
                truck.getRegisterPlateNumber(),
                truck.getBusinessId(),
                truck.getStartPeriodDate(),
                truck.getEndPeriodDate(),
                truck.getCurrentTrailerBusinessId());
    }
}
