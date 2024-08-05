package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
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
    public TrailerDTO addTrailer(AddTrailerCommand newTrailer) {
        return convert(trailerRepository.save(new Trailer(
                UUID.randomUUID(),
                newTrailer.registerPlateNumber())));
    }

    @Override
    public TrailerDTO getTrailerByBusinessId(UUID uuid) {
        return convert(trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid.toString())));
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
    public TrailerDTO updateTrailerByBusinessId(UUID uuid, UpdateTrailerCommand updateTrailerCommand) {
        var trailer = trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid.toString()));
//
//        trailer.setInUse(updateTrailerCommand.isUsed());
//        trailer.setCrossHitch(updateTrailerCommand.isCrossHitch());
//        trailer.setStartPeriodDate(updateTrailerCommand.startPeriod());
//        trailer.setEndPeriodDate(updateTrailerCommand.endPeriod());
////        trailer.setTrucks(updateTrailerCommand.trucks()); TODO
//
//        //TODO tutaj dodac zmiane rekordow w tabeli truck_trailer

        return convert(trailer);
    }

    @Transactional
    @Override
    public String crossHitchOperation(UUID processingTrailerBusinessId, UpdateTrailerCommand updateTrailerCommand) {
        StringBuilder result = new StringBuilder();

        Optional<Trailer> processingTrailer = trailerRepository.findByBusinessId(processingTrailerBusinessId);
        Optional<Truck> processingTrailerCurrentTruck = truckRepository.findByBusinessId(processingTrailer.get().getCurrentTruckBusinessId());

        // aktualizacja wartosci procesowanej naczepy
        if (processingTrailer.isPresent()) {
            if (updateTrailerCommand.isCrossHitch().isPresent())
                processingTrailer.get().setCrossHitch(updateTrailerCommand.isCrossHitch().get());
            if (updateTrailerCommand.startPeriod().isPresent())
                processingTrailer.get().setStartPeriodDate(updateTrailerCommand.startPeriod().get());
            if (updateTrailerCommand.endPeriod().isPresent())
                processingTrailer.get().setEndPeriodDate(updateTrailerCommand.endPeriod().get());
            if (updateTrailerCommand.truckId().isPresent())
                processingTrailer.get().setCurrentTruckBusinessId(updateTrailerCommand.truckId().get());
        } else {
            return "Trailer with business id " + processingTrailerBusinessId.toString() + " not found";
        }

        Optional<Truck> crossHitchTruck = truckRepository.findByBusinessId(updateTrailerCommand.truckId().get());
        UUID currentTrailerAssignmentToTruck2;

        // aktualizacja wartosci pojazdu ktory bedzie nowym przypisanem pojazdem do procesowanej naczepy
        if (crossHitchTruck.isPresent()) {
            currentTrailerAssignmentToTruck2 = crossHitchTruck.get().getCurrentTrailerBusinessId();
            if (updateTrailerCommand.startPeriod().isPresent()) crossHitchTruck.get().setStartPeriodDate(updateTrailerCommand.startPeriod().get());
            if (updateTrailerCommand.endPeriod().isPresent()) crossHitchTruck.get().setEndPeriodDate(updateTrailerCommand.endPeriod().get());
            crossHitchTruck.get().setCurrentTrailerBusinessId(processingTrailerBusinessId);
        } else {
            return "Truck with business id " + processingTrailerBusinessId.toString() + " not found";
        }

        TruckTrailerHistory crossHitchOperation = new TruckTrailerHistory();
        crossHitchOperation.setTrailer(processingTrailer.get());
        crossHitchOperation.setTruck(crossHitchTruck.get());
        crossHitchOperation.setStartPeriodDate(updateTrailerCommand.startPeriod().orElse(null));
        crossHitchOperation.setEndPeriodDate(updateTrailerCommand.endPeriod().orElse(null));
        tthRepository.save(crossHitchOperation);

        Optional<Trailer> crossHitchTrailer = trailerRepository.findByBusinessId(currentTrailerAssignmentToTruck2);

        // aktualizacja wartosci drugiego zestawu operacji cross hitch
        if (crossHitchTrailer.isPresent()) {
            if (crossHitchTrailer.get().isCrossHitch()) {
                crossHitchTrailer.get().setCurrentTruckBusinessId(processingTrailerCurrentTruck.get().getBusinessId());

                processingTrailerCurrentTruck.get().setCurrentTrailerBusinessId(crossHitchTrailer.get().getBusinessId());

                TruckTrailerHistory crossHitchOperation2 = new TruckTrailerHistory();
                crossHitchOperation2.setTrailer(crossHitchTrailer.get());
                crossHitchOperation2.setTruck(processingTrailerCurrentTruck.get());
                crossHitchOperation2.setStartPeriodDate(crossHitchTrailer.get().getStartPeriodDate());
                crossHitchOperation2.setEndPeriodDate(crossHitchTrailer.get().getEndPeriodDate());

                tthRepository.save(crossHitchOperation2);
            } else {
                result.append(" Second trailer is not cross hitch available and will be unassigned from any truck - truck assignment to processing trailer before cross hitch operation now will be unassigned to any trailer");
                crossHitchTrailer.get().setStartPeriodDate(null);
                crossHitchTrailer.get().setEndPeriodDate(null);
                crossHitchTrailer.get().setCurrentTruckBusinessId(null);

                processingTrailerCurrentTruck.get().setCurrentTrailerBusinessId(null);
            }
        } else {
            result.append(" Second truck has no assignment trailer, proccessing trailer current truck now will be unassigned to any trailer.");
            processingTrailerCurrentTruck.get().setCurrentTrailerBusinessId(null);
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
