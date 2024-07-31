package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrailerService implements TrailerApi {

    private final TrailerRepository trailerRepository;


    @Override
    public TrailerDTO addTrailer(AddTrailerCommand newTrailer) {
        return null;
    }

    @Override
    public Optional<TrailerDTO> getTrailerByBusinessId(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public Optional<List<TrailerDTO>> getAllTrailers() {
        return Optional.empty();
    }

    @Override
    public TrailerDTO updateTrailerByBusinessId(UUID uuid, UpdateTrailerCommand newTrailer) {
        return null;
    }

    @Override
    public void deleteTrailerByBusinessId(UUID uuid) {

    }
}
