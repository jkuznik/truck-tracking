package pl.jkuznik.trucktracking.domain.trailer.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.UUID;

@Builder
@Validated
public record TrailerDTO(
        @NotNull String trailerPlateNumber,
        UUID businessId,
        Boolean isCrossHitch,
        Instant startPeriod,
        Instant endPeriod,
        UUID currentTruckBusinessId) {
}
