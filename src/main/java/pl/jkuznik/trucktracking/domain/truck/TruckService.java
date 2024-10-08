package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.shared.PlateNumberExistException;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateAssignmentCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepositoryImpl;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class TruckService implements TruckApi {

    private final Integer DEFAULT_PAGE_NUMBER = 0;
    private final Integer DEFAULT_PAGE_SIZE = 25;

    private final TruckRepository truckRepository;
    private final TrailerRepository trailerRepository;
    private final TTHRepositoryImpl tthRepository;

    @Transactional
    @Override
    public TruckDTO addTruck(AddTruckCommand newTruck) {

        Optional<Truck> byRegisterPlateNumber = truckRepository.findByRegisterPlateNumber(newTruck.registerPlateNumber());

        if (byRegisterPlateNumber.isPresent()) {
            throw new PlateNumberExistException("Truck with " + newTruck.registerPlateNumber() + " plate number already exists");
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

        PageRequest pageRequest = getPageRequest(pageNumber, pageSize);

        List<TruckDTO> list = tthRepository.getTruckUsedInLastMonth(pageRequest).stream()
                .map(this::convert)
                .toList();

        return new PageImpl<>(list);

    }

    @Transactional
    @Override
    public TruckDTO updateTruckAssignByBusinessId(UUID uuid, UpdateAssignmentCommand updateAssignmentCommand) {
        Truck truck = truckRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " + uuid.toString() + " not found"));

        Trailer trailer;

        if (updateAssignmentCommand.trailerId().isPresent()) {
            trailer = trailerRepository.findByBusinessId(updateAssignmentCommand.trailerId().get())
                    .orElseThrow(() -> new NoSuchElementException("No trailer with id " + updateAssignmentCommand.trailerId().get()));
        } else {
            throw new NoSuchElementException("Trailer business id is needed in this operation");
        }

        try {
            trailer.isInUse(updateAssignmentCommand.startPeriod().orElse(null), updateAssignmentCommand.endPeriod().orElse(null));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (updateAssignmentCommand.startPeriod().isPresent() && updateAssignmentCommand.endPeriod().isPresent()) {
            if (updateAssignmentCommand.endPeriod().get().isBefore(updateAssignmentCommand.startPeriod().get())) {
                try {
                    throw new Exception("End period is before start period");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        truck.setStartPeriodDate(updateAssignmentCommand.startPeriod().orElse(null));
        truck.setEndPeriodDate(updateAssignmentCommand.endPeriod().orElse(null));
        if (updateAssignmentCommand.startPeriod().isEmpty() && updateAssignmentCommand.endPeriod().isEmpty()) {
            truck.setCurrentTrailerBusinessId(null);
        } else {
            truck.setCurrentTrailerBusinessId(trailer.getBusinessId());
        }

        trailer.setStartPeriodDate(updateAssignmentCommand.startPeriod().orElse(null));
        trailer.setEndPeriodDate(updateAssignmentCommand.endPeriod().orElse(null));
        if (updateAssignmentCommand.startPeriod().isEmpty() && updateAssignmentCommand.endPeriod().isEmpty()) {
            trailer.setCurrentTruckBusinessId(null);
        } else {
            trailer.setCurrentTruckBusinessId(truck.getBusinessId());
        }

        var tth = new TruckTrailerHistory();

        tth.setTrailer(trailer);
        tth.setTruck(truck);
        if (updateAssignmentCommand.startPeriod().isPresent())
            tth.setStartPeriodDate(updateAssignmentCommand.startPeriod().get());
        if (updateAssignmentCommand.endPeriod().isPresent())
            tth.setEndPeriodDate(updateAssignmentCommand.endPeriod().get());

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

    private PageRequest getPageRequest(Integer pageNumber, Integer pageSize) {
        int number;
        int size;

        if (pageNumber != null && pageNumber > 0) {
            number = pageNumber -1;
        } else {
            number = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize != null && pageSize > 25) {
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
}
