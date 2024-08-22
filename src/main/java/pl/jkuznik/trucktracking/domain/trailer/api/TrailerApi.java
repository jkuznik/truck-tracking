package pl.jkuznik.trucktracking.domain.trailer.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UnassignTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.util.List;
import java.util.UUID;

@Validated
public interface TrailerApi {

    TrailerDTO addTrailer(@NotNull @Valid AddTrailerCommand addTrailerCommand);

    TrailerDTO getTrailerByBusinessId(@NotNull UUID uuid);
    Page<TrailerDTO> getAllTrailers(Integer pageNumber, Integer pageSize);
    List<TrailerDTO> getTrailersByCrossHitch(@NotNull Boolean crossHitch);

    @Transactional
    TrailerDTO updateCrossHitchTrailerValue(@NotNull UUID uuid, @NotNull @Valid UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand);

    @Transactional
    TrailerDTO unassignTrailerByBusinessId(@NotNull UUID uuid, @NotNull @Valid UnassignTrailerCommand unassignTrailerCommand);

    @Transactional
    TrailerDTO assignTrailerByBusinessId(@NotNull UUID uuid, @NotNull UpdateAssignmentTrailerCommand newTrailer);

    @Transactional
    String crossHitchOperation(@NotNull UUID uuid, @NotNull @Valid UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand);

    @Transactional
    void deleteTrailerByBusinessId(@NotNull UUID uuid);
}
