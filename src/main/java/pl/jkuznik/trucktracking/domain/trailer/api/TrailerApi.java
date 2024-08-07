package pl.jkuznik.trucktracking.domain.trailer.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UnassignTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Validated
public interface TrailerApi {

    TrailerDTO addTrailer(@NotNull @Valid AddTrailerCommand addTrailerCommand);

    TrailerDTO getTrailerByBusinessId(@NotNull UUID uuid);
    List<TrailerDTO> getAllTrailers();
    List<TrailerDTO> getTrailersByStartPeriodDate(Instant startDate);
    List<TrailerDTO> getTrailersByEndPeriodDate(Instant endDate);
    List<TrailerDTO> getTrailersByCrossHitch(@NotNull Boolean crossHitch);

    @Transactional
    TrailerDTO updateTrailerByBusinessId(@NotNull UUID uuid, @Valid UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand);

    @Transactional
    TrailerDTO unassignTrailerManageByBusinessId(@NotNull UUID uuid, @Valid UnassignTrailerCommand unassignTrailerCommand);

    @Transactional
    TrailerDTO assignTrailerManageByBusinessId(@NotNull UUID uuid,@NotNull @Valid UpdateAssignmentTrailerCommand newTrailer);

    @Transactional
    String crossHitchOperation(@NotNull UUID uuid, @NotNull @Valid UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand);

    @Transactional
    void deleteTrailerByBusinessId(@NotNull UUID uuid);
}
