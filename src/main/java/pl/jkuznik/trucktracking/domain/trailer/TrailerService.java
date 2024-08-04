package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
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
    private final TTHRepository tthRepository;

    public List<TrailerDTO> getTrailersByFilters(Optional<Instant> startDate, Optional<Instant> endDate,
                                                 Optional<Boolean> inUse, Optional<Boolean> crossHitch) {
        List<Trailer> filteredTrailers = trailerRepository.findAll();

        if (startDate.isPresent()) {
            List<Trailer> allByStartPeriodDate = trailerRepository.findAllByStartPeriodDate(startDate.get());
            filteredTrailers.retainAll(allByStartPeriodDate);
        }
        if (endDate.isPresent()) {
            List<Trailer> allByEndPeriodDate = trailerRepository.findAllByEndPeriodDate(endDate.get());
            filteredTrailers.retainAll(allByEndPeriodDate);
        }
        if (inUse.isPresent()) {
            List<Trailer> allByInUse = trailerRepository.findAllByInUse(inUse.get());
            filteredTrailers.retainAll(allByInUse);
        }
        if (crossHitch.isPresent()) {
            List<Trailer> allByCrossHitch = trailerRepository.findAllByCrossHitch(crossHitch.get());
            filteredTrailers.retainAll(allByCrossHitch);
        }

        return filteredTrailers.stream()
                .map(this::convert)
                .toList();
    }

    public List<TruckTrailerHistoryDTO> getTrailersHistoryByFilters(Optional<Instant> startDate, Optional<Instant> endDate,
                                                                    Optional<UUID> truckId, Optional<UUID> trailerID) { //todo argumenty przygotowane do pobierania info o pojazdach i naczepach

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
    public List<TrailerDTO> getTrailersByInUsed(Boolean inUsed) {
        return trailerRepository.findAllByInUse(inUsed).stream()
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
    public void deleteTrailerByBusinessId(UUID uuid) {
        trailerRepository.deleteByBusinessId(uuid);
    }

    private TrailerDTO convert(Trailer trailer) {
        return new TrailerDTO(
                trailer.getRegisterPlateNumber(),
                trailer.getBusinessId(),
                trailer.isInUse(),
                trailer.isCrossHitch(),
                trailer.getStartPeriodDate(),
                trailer.getEndPeriodDate());
    }

}
