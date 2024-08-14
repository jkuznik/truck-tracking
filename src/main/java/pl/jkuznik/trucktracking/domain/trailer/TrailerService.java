package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UnassignTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.impl.TTHRepositoryImpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class TrailerService implements TrailerApi {

    private final Integer DEFAULT_PAGE_NUMBER = 0;
    private final Integer DEFAULT_PAGE_SIZE = 25;

    private final TrailerRepository trailerRepository;
    private final TruckRepository truckRepository;
    private final TTHRepositoryImpl tthRepository;

    @Override
    public TrailerDTO addTrailer(AddTrailerCommand addTrailerCommand) {
        Optional<Trailer> existTrailer = trailerRepository.findByRegisterPlateNumber(addTrailerCommand.registerPlateNumber());
        if (existTrailer.isPresent()) {
            throw new IllegalStateException("Trailer with " + addTrailerCommand.registerPlateNumber() + " plate number already exists");
        }

        return convert(trailerRepository.save(new Trailer(
                UUID.randomUUID(),
                addTrailerCommand.registerPlateNumber())));
    }

    @Override
    public TrailerDTO getTrailerByBusinessId(UUID uuid) {
        return convert(trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid)));
    }

    @Override
    public Page<TrailerDTO> getAllTrailers(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = getPageRequest(pageNumber, pageSize);

        return new PageImpl<>(trailerRepository.findAll(pageRequest).stream()
                .map(this::convert)
                .toList());
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

    @Override
    public List<TrailerDTO> getTrailersByCrossHitch(Boolean crossHitch) {
        return trailerRepository.findAllByCrossHitch(crossHitch).stream()
                .map(this::convert)
                .toList();
    }

    @Transactional
    @Override
    public TrailerDTO updateCrossHitchTrailerByBusinessId(UUID uuid, UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand) {
        Trailer trailer = trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid));

        trailer.setCrossHitch(updateCrossHitchTrailerCommand.crossHitch());

        return convert(trailer);
    }

    @Transactional
    @Override
    public TrailerDTO unassignTrailerByBusinessId(UUID uuid, UnassignTrailerCommand unassignTrailerCommand) {
        Trailer trailer = trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid));

        Truck truck;

        if (trailer.getCurrentTruckBusinessId() == null) {
            throw new IllegalStateException("Current trailer is already unassigned");
        }

        if (unassignTrailerCommand.isTruckStillExist()) {
            truck = truckRepository.findByBusinessId(trailer.getCurrentTruckBusinessId())
                    .orElseThrow(() ->
                            new NoSuchElementException("No truck with business id " + trailer.getCurrentTruckBusinessId() +
                                    "Consider to switch 'isTruckStillExist' as false"));
        } else {
            truck = null;
        }

        trailer.setStartPeriodDate(null);
        trailer.setEndPeriodDate(null);
        trailer.setCurrentTruckBusinessId(null);

        if (truck != null) {
            truck.setStartPeriodDate(null);
            truck.setEndPeriodDate(null);
            truck.setCurrentTrailerBusinessId(null);
        }

        return convert(trailer);
    }

    @Transactional
    @Override
    public TrailerDTO assignTrailerByBusinessId(UUID uuid, UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand) {
        // TODO dodać obsługę wyjątków
        if (updateAssignmentTrailerCommand.startPeriod().isEmpty() &&
                updateAssignmentTrailerCommand.endPeriod().isEmpty() && updateAssignmentTrailerCommand.truckId().isPresent()) {
            throw new IllegalStateException("Wrong operation to unassign a truck");
        }

        Trailer trailer = trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid));
        Truck truck;

        if (updateAssignmentTrailerCommand.truckId().isPresent()) {
            truck = truckRepository.findByBusinessId(updateAssignmentTrailerCommand.truckId().get())
                    .orElseThrow(() -> new NoSuchElementException("No truck with id " + updateAssignmentTrailerCommand.truckId().get()));
        } else {
            throw new NoSuchElementException("Truck business id is needed in this operation");
        }

        try {
            if (trailer.isInUse(updateAssignmentTrailerCommand.startPeriod().orElse(null), updateAssignmentTrailerCommand.endPeriod().orElse(null))) {
                throw new IllegalStateException("The trailer is in use during the specified period.");
            }
        } catch (IllegalStateException e) {
            throw e;
        }

        if (updateAssignmentTrailerCommand.startPeriod().isPresent() && updateAssignmentTrailerCommand.endPeriod().isPresent()) {
            if (updateAssignmentTrailerCommand.endPeriod().get().isBefore(updateAssignmentTrailerCommand.startPeriod().get())) {
                try {
                    throw new IllegalStateException("End period is before start period");
                } catch (IllegalStateException e) {
                    throw e;
                }
            }
        }

        if (updateAssignmentTrailerCommand.isCrossHitch().isPresent())
            trailer.setCrossHitch(updateAssignmentTrailerCommand.isCrossHitch().get());
        trailer.setStartPeriodDate(updateAssignmentTrailerCommand.startPeriod().orElse(null));
        trailer.setEndPeriodDate(updateAssignmentTrailerCommand.endPeriod().orElse(null));
        trailer.setCurrentTruckBusinessId(updateAssignmentTrailerCommand.truckId().orElse(null));


        truck.setStartPeriodDate(updateAssignmentTrailerCommand.startPeriod().orElse(null));
        truck.setEndPeriodDate(updateAssignmentTrailerCommand.endPeriod().orElse(null));
        truck.setCurrentTrailerBusinessId(trailer.getBusinessId());


        var tth = new TruckTrailerHistory();

        tth.setTrailer(trailer);
        tth.setTruck(truck);
        if (updateAssignmentTrailerCommand.startPeriod().isPresent())
            tth.setStartPeriodDate(updateAssignmentTrailerCommand.startPeriod().get());
        if (updateAssignmentTrailerCommand.endPeriod().isPresent())
            tth.setEndPeriodDate(updateAssignmentTrailerCommand.endPeriod().get());

        trailerRepository.save(trailer);
        truckRepository.save(truck);
        tthRepository.save(tth);
        return convert(trailer);
    }

    @Transactional
    @Override
    public String crossHitchOperation(UUID processingTrailerBusinessId, UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand) {
        StringBuilder result = new StringBuilder();

        Trailer processingTrailer = trailerRepository.findByBusinessId(processingTrailerBusinessId)
                .orElseThrow(() -> new NoSuchElementException("No trailer with id " + processingTrailerBusinessId));
        Truck processingTrailerCurrentTruck = truckRepository.findByBusinessId(processingTrailer.getCurrentTruckBusinessId())
                .orElseThrow(() -> new NoSuchElementException("No truck with id " + processingTrailer.getCurrentTruckBusinessId()));

        // aktualizacja wartosci procesowanej naczepy
        if (updateAssignmentTrailerCommand.isCrossHitch().isPresent())
            processingTrailer.setCrossHitch(updateAssignmentTrailerCommand.isCrossHitch().get());
        if (updateAssignmentTrailerCommand.startPeriod().isPresent())
            processingTrailer.setStartPeriodDate(updateAssignmentTrailerCommand.startPeriod().get());
        if (updateAssignmentTrailerCommand.endPeriod().isPresent())
            processingTrailer.setEndPeriodDate(updateAssignmentTrailerCommand.endPeriod().get());
        if (updateAssignmentTrailerCommand.truckId().isPresent())
            processingTrailer.setCurrentTruckBusinessId(updateAssignmentTrailerCommand.truckId().get());


        Truck crossHitchTruck = truckRepository.findByBusinessId(updateAssignmentTrailerCommand.truckId().get())
                .orElseThrow(() -> new NoSuchElementException("Truck with business id " +
                        updateAssignmentTrailerCommand.truckId().get() + " aimed to cross hitch operation doesnt exist  "));
        UUID currentTrailerBusinessIdAssignmentToTruck2;

        // aktualizacja wartosci pojazdu ktory bedzie nowym przypisanem pojazdem do procesowanej naczepy
        currentTrailerBusinessIdAssignmentToTruck2 = crossHitchTruck.getCurrentTrailerBusinessId();
        if (updateAssignmentTrailerCommand.startPeriod().isPresent())
            crossHitchTruck.setStartPeriodDate(updateAssignmentTrailerCommand.startPeriod().get());
        if (updateAssignmentTrailerCommand.endPeriod().isPresent())
            crossHitchTruck.setEndPeriodDate(updateAssignmentTrailerCommand.endPeriod().get());
        crossHitchTruck.setCurrentTrailerBusinessId(processingTrailerBusinessId);

        TruckTrailerHistory crossHitchOperation = new TruckTrailerHistory();
        crossHitchOperation.setTrailer(processingTrailer);
        crossHitchOperation.setTruck(crossHitchTruck);
        crossHitchOperation.setStartPeriodDate(updateAssignmentTrailerCommand.startPeriod().orElse(null));
        crossHitchOperation.setEndPeriodDate(updateAssignmentTrailerCommand.endPeriod().orElse(null));
        tthRepository.save(crossHitchOperation);

        Optional<Trailer> crossHitchTrailer = trailerRepository.findByBusinessId(currentTrailerBusinessIdAssignmentToTruck2);

        // aktualizacja wartosci drugiego zestawu operacji cross hitch
        if (crossHitchTrailer.isPresent()) {
            if (crossHitchTrailer.get().isCrossHitch()) {
                crossHitchTrailer.get().setCurrentTruckBusinessId(processingTrailerCurrentTruck.getBusinessId());

                processingTrailerCurrentTruck.setCurrentTrailerBusinessId(crossHitchTrailer.get().getBusinessId());
                processingTrailerCurrentTruck.setStartPeriodDate(crossHitchTrailer.get().getStartPeriodDate());
                processingTrailerCurrentTruck.setEndPeriodDate(crossHitchTrailer.get().getEndPeriodDate());

                TruckTrailerHistory crossHitchOperation2 = new TruckTrailerHistory();
                crossHitchOperation2.setTrailer(crossHitchTrailer.get());
                crossHitchOperation2.setTruck(processingTrailerCurrentTruck);
                crossHitchOperation2.setStartPeriodDate(crossHitchTrailer.get().getStartPeriodDate());
                crossHitchOperation2.setEndPeriodDate(crossHitchTrailer.get().getEndPeriodDate());

                tthRepository.save(crossHitchOperation2);
            } else {
                result.append("Second trailer is not cross hitch available and will be unassigned from any truck - truck assignment to processing trailer before cross hitch operation now will be unassigned to any trailer");
                crossHitchTrailer.get().setStartPeriodDate(null);
                crossHitchTrailer.get().setEndPeriodDate(null);
                crossHitchTrailer.get().setCurrentTruckBusinessId(null);

                processingTrailerCurrentTruck.setCurrentTrailerBusinessId(null);
                processingTrailerCurrentTruck.setStartPeriodDate(null);
                processingTrailerCurrentTruck.setEndPeriodDate(null);
            }
        } else {
            result.append("Second truck has no assignment trailer, proccessing trailer current truck now will be unassigned to any trailer.");
            processingTrailerCurrentTruck.setCurrentTrailerBusinessId(null);
            processingTrailerCurrentTruck.setStartPeriodDate(null);
            processingTrailerCurrentTruck.setEndPeriodDate(null);
        }

        result.insert(0, "Cross hitch operation on processing trailer success. ");

        return result.toString();
    }

    //TODO dopisac testy if trailer not exist
    @Transactional
    @Override
    public void deleteTrailerByBusinessId(UUID uuid) {
        Trailer trailer = trailerRepository.findByBusinessId(uuid).orElseThrow(
                () -> new NoSuchElementException("Trailer with business id " + uuid + " not found")
        );

        if (trailer.getCurrentTruckBusinessId() != null) {
            Optional<Truck> truck = truckRepository.findByBusinessId(trailer.getCurrentTruckBusinessId());

            if (truck.isPresent()) {
                truck.get().setStartPeriodDate(null);
                truck.get().setEndPeriodDate(null);
                truck.get().setCurrentTrailerBusinessId(null);

                truckRepository.save(truck.get());
            }
        }

        trailerRepository.deleteByBusinessId(uuid);
    }

    private TrailerDTO convert(Trailer trailer) {
        return new TrailerDTO(
                trailer.getRegisterPlateNumber(),
                trailer.getBusinessId(),
                trailer.isCrossHitch(),
                trailer.getStartPeriodDate(),
                trailer.getEndPeriodDate(),
                trailer.getCurrentTruckBusinessId());
    }

}
