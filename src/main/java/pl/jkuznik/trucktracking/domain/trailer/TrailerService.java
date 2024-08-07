package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class TrailerService implements TrailerApi {

    private final TrailerRepository trailerRepository;
    private final TruckRepository truckRepository;
    private final TTHRepository tthRepository;

    public List<TrailerDTO> getTrailersByFilters(Optional<Instant> startDate,
                                                 Optional<Instant> endDate,
                                                 Optional<Boolean> crossHitch) {
        List<Trailer> filteredTrailers = trailerRepository.findAll();

        if (startDate.isPresent()) {
            List<Trailer> allByStartPeriodDate = trailerRepository.findAllByStartPeriodDate(startDate.get());
            filteredTrailers.retainAll(allByStartPeriodDate);
        }
        if (endDate.isPresent()) {
            List<Trailer> allByEndPeriodDate = trailerRepository.findAllByEndPeriodDate(endDate.get());
            filteredTrailers.retainAll(allByEndPeriodDate);
        }
        if (crossHitch.isPresent()) {
            List<Trailer> allByCrossHitch = trailerRepository.findAllByCrossHitch(crossHitch.get());
            filteredTrailers.retainAll(allByCrossHitch);
        }

        return filteredTrailers.stream()
                .map(this::convert)
                .toList();
    }

    public List<TruckTrailerHistoryDTO> getTrailersHistoryByFilters(Optional<Instant> startDate,
                                                                    Optional<Instant> endDate,
                                                                    Optional<UUID> truckId,
                                                                    Optional<UUID> trailerID) { //todo argumenty przygotowane do pobierania info o pojazdach i naczepach

        List<TruckTrailerHistoryDTO> filteredTrailers = tthRepository.findAll().stream()
                .map(TruckTrailerHistory::convert)
                .collect(Collectors.toList());

        if (startDate.isPresent()) {
            List<TruckTrailerHistoryDTO> allByStartPeriodDate = tthRepository.findAllByStartPeriodDate(startDate.get()).stream()
                    .map(TruckTrailerHistory::convert)
                    .toList();
            filteredTrailers.retainAll(allByStartPeriodDate);
        }
        if (endDate.isPresent()) {
            List<TruckTrailerHistoryDTO> allByEndPeriodDate = tthRepository.findAllByEndPeriodDate(endDate.get()).stream()
                    .map(TruckTrailerHistory::convert)
                    .toList();
            filteredTrailers.retainAll(allByEndPeriodDate);
        }

        return filteredTrailers;
    }

    @Override
    public TrailerDTO addTrailer(AddTrailerCommand addTrailerCommand) {
        Optional<Trailer> existTrailer = trailerRepository.findByRegisterPlateNumber(addTrailerCommand.registerPlateNumber());
        if (existTrailer.isPresent()) {
            throw new RuntimeException("Trailer with " + addTrailerCommand.registerPlateNumber() + " plate number already exists");  //TODO działa ale poprawic bo leci status 500
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
    public List<TrailerDTO> getAllTrailers() {
        return trailerRepository.findAll().stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<TrailerDTO> getTrailersByStartPeriodDate(Instant startDate) {
        return trailerRepository.findAllByStartPeriodDate(startDate).stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<TrailerDTO> getTrailersByEndPeriodDate(Instant endDate) {
        return trailerRepository.findAllByEndPeriodDate(endDate).stream()
                .map(this::convert)
                .toList();
    }

    @Override
    public List<TrailerDTO> getTrailersByCrossHitch(Boolean crossHitch) {
        return trailerRepository.findAllByCrossHitch(crossHitch).stream()
                .map(this::convert)
                .toList();
    }

    @Transactional
    @Override
    public TrailerDTO updateTrailerByBusinessId(UUID uuid, UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand) throws Exception {
        Trailer trailer = trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid));

        trailer.setCrossHitch(updateCrossHitchTrailerCommand.crossHitch());

        return convert(trailer);
    }

    @Transactional
    @Override
    public TrailerDTO assignTrailerManageByBusinessId(UUID uuid, UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand) {
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

                TruckTrailerHistory crossHitchOperation2 = new TruckTrailerHistory();
                crossHitchOperation2.setTrailer(crossHitchTrailer.get());
                crossHitchOperation2.setTruck(processingTrailerCurrentTruck);
                crossHitchOperation2.setStartPeriodDate(crossHitchTrailer.get().getStartPeriodDate());
                crossHitchOperation2.setEndPeriodDate(crossHitchTrailer.get().getEndPeriodDate());

                tthRepository.save(crossHitchOperation2);
            } else {
                result.append(" Second trailer is not cross hitch available and will be unassigned from any truck - truck assignment to processing trailer before cross hitch operation now will be unassigned to any trailer");
                crossHitchTrailer.get().setStartPeriodDate(null);
                crossHitchTrailer.get().setEndPeriodDate(null);
                crossHitchTrailer.get().setCurrentTruckBusinessId(null);

                processingTrailerCurrentTruck.setCurrentTrailerBusinessId(null);
            }
        } else {
            result.append(" Second truck has no assignment trailer, proccessing trailer current truck now will be unassigned to any trailer.");
            processingTrailerCurrentTruck.setCurrentTrailerBusinessId(null);
        }

        result.insert(0, "Cross hitch operation on processing trailer success. ");

        return result.toString();
    }

    @Transactional
    @Override
    public void deleteTrailerByBusinessId(UUID uuid) {
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
