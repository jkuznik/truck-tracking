package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
class TrailerService implements TrailerApi {

    private final TrailerRepository trailerRepository;
    private final TTHRepository tthRepository;

    @Override
    public TrailerDTO addTrailer(AddTrailerCommand newTrailer) {
        return convert(trailerRepository.save(new Trailer(
                newTrailer.registerPlateNumber(),
                UUID.randomUUID(),
                newTrailer.length(),
                newTrailer.height(),
                newTrailer.weight())));
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

    public List<TrailerDTO> getTrailersByFilters(Optional<Instant> startDate, Optional<Instant> endDate,
                                                 Optional<Boolean> inUse, Optional<Boolean> crossHitch) {
        List<Trailer> filteredTrailers = trailerRepository.findAll();
        List<Trailer> allByInUse = new ArrayList<>();
        List<Trailer> allByCrossHitch = new ArrayList<>();
        List<Trailer> allByStartPeriodDate = new ArrayList<>();
        List<Trailer> allByEndPeriodDate = new ArrayList<>();

        if (inUse.isPresent()) {
            allByInUse.addAll(trailerRepository.findAllByInUse(inUse.get()));
        }
        if (crossHitch.isPresent()) {
            allByCrossHitch.addAll(trailerRepository.findAllByCrossHitch(crossHitch.get()));
        }
        if (startDate.isPresent()) {

            List<Long> idList = tthRepository.findAllByStartPeriodDate(startDate.get()).stream()
                    .map(TruckTrailerHistory::convert)
                    .map(TruckTrailerHistoryDTO::trailerId)
                    .toList();

            allByStartPeriodDate.addAll(trailerRepository.findAllById(idList));
        }

        if (endDate.isPresent()) {
            List<Long> idList = tthRepository.findAllByEndPeriodDate(endDate.get()).stream()
                    .map(TruckTrailerHistory::convert)
                    .map(TruckTrailerHistoryDTO::trailerId)
                    .toList();

            allByEndPeriodDate.addAll(trailerRepository.findAllById(idList));
        }

        if (!allByInUse.isEmpty()) filteredTrailers.retainAll(allByInUse);
        if (!allByCrossHitch.isEmpty()) filteredTrailers.retainAll(allByCrossHitch);
        if (!allByStartPeriodDate.isEmpty()) filteredTrailers.retainAll(allByStartPeriodDate);
        if (!allByEndPeriodDate.isEmpty()) filteredTrailers.retainAll(allByEndPeriodDate);

        return filteredTrailers.stream()
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
    public List<TrailerDTO> getTrailersByInUsed(boolean inUsed) {
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
                trailer.getEndPeriodDate(),
                trailer.getLength(),
                trailer.getHeight(),
                trailer.getWeight());
    }

}
