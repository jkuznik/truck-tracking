package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record UpdateTrailerCommand(
        @NotNull Boolean isUsed,
        @NotNull Instant startPeriod,
        @NotNull Instant endPeriod) {
}
