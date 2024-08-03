package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class TrailerService implements TrailerApi {

    private final TrailerRepository trailerRepository;

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

    @Transactional
    @Override
    public TrailerDTO updateTrailerByBusinessId(UUID uuid, UpdateTrailerCommand updateTrailerCommand) {
        var trailer = trailerRepository.findByBusinessId(uuid)
                .orElseThrow(() -> new NoSuchElementException("No trailer with business id " + uuid.toString()));

        trailer.setInUse(updateTrailerCommand.isUsed());
        trailer.setCrossHitch(updateTrailerCommand.isCrossHitch());
        trailer.setStartPeriod(updateTrailerCommand.startPeriod());
        trailer.setEndPeriod(updateTrailerCommand.endPeriod());
        trailer.setTrucks(updateTrailerCommand.trucks());

        //TODO tutaj dodac zmiane rekordow w tabeli truck_trailer

        return convert(trailer);
    }

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
                trailer.getStartPeriod(),
                trailer.getEndPeriod(),
                trailer.getLength(),
                trailer.getHeight(),
                trailer.getWeight());
    }
}
