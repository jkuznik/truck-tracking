package pl.jkuznik.trucktracking.domain.trailer.api;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Validated
public interface TrailerApi {

    TrailerDTO addTrailer(@Valid AddTrailerCommand newTrailer);

    TrailerDTO getTrailerByBusinessId(UUID uuid);
    List<TrailerDTO> getAllTrailers();

    TrailerDTO updateTrailerByBusinessId(UUID uuid, @Valid UpdateTrailerCommand newTrailer);

    void deleteTrailerByBusinessId(UUID uuid);
}
