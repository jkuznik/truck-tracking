package pl.jkuznik.trucktracking.domain.truck;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class TruckService implements TruckApi {

    private final TruckRepository truckRepository;
    private final TrailerRepository trailerRepository;
    private final TTHRepository tthRepository;

    @Override
    public TruckDTO addTruck(AddTruckCommand newTruck) {
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
    public List<TruckDTO> getAllTrucks(boolean lastMonth) {
        List<TruckDTO> trucks = new ArrayList<>();

        if (lastMonth) {
            Instant now = Instant.now();
            ZonedDateTime zonedDateTimeNow = now.atZone(ZoneId.of("UTC"));
            ZonedDateTime zonedDateTimePastMonth = zonedDateTimeNow.minusMonths(1);
            Instant date = zonedDateTimePastMonth.toInstant();

            trucks = tthRepository.findByUsingInLastMonth(date).stream()
                    .map(TruckTrailerHistory::getTruck)
                    .collect(Collectors.toList()).stream()
                    .map(this::convert)
                    .toList();


        } else {
            trucks = truckRepository.findAll().stream()
                    .map(this::convert)
                    .collect(Collectors.toList());
        }


        return trucks;
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
                // TODO wyświetlić komunikat o tym że pojazd był aktualnie przypisany do naczepy
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
