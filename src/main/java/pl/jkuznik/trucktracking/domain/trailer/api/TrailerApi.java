package pl.jkuznik.trucktracking.domain.trailer.api;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpadeteAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Validated
public interface TrailerApi {

    TrailerDTO addTrailer(@Valid AddTrailerCommand newTrailer);

    TrailerDTO getTrailerByBusinessId(UUID uuid);
    List<TrailerDTO> getAllTrailers();
    List<TrailerDTO> getTrailersByStartPeriodDate(Instant startDate);
    List<TrailerDTO> getTrailersByEndPeriodDate(Instant endDate);
    List<TrailerDTO> getTrailersByCrossHitch(Boolean crossHitch);

    @Transactional
    TrailerDTO assignTrailerManageByBusinessId(UUID uuid, @Valid UpadeteAssignmentTrailerCommand newTrailer);

    @Transactional
    void deleteTrailerByBusinessId(UUID uuid);

    String crossHitchOperation(UUID uuid, UpadeteAssignmentTrailerCommand upadeteAssignmentTrailerCommand);

    TrailerDTO updateTrailerByBusinessId(UUID uuid, UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand) throws Exception;
}
