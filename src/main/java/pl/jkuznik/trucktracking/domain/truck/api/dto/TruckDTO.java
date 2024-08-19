package pl.jkuznik.trucktracking.domain.truck.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.UUID;

@Builder
@Validated
public record TruckDTO(
        @NotNull String truckPlateNumber,
        UUID businessId,
        Instant startPeriod,
        Instant endPeriod,
        UUID currentTrailerBusinessId) {
}
