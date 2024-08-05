package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record UpdateTrailerCommand(
        @NotNull Boolean isUsed,
        Optional<Boolean> isCrossHitch,
        Optional<Instant> startPeriod,
        Optional<Instant> endPeriod,
        Optional<UUID> truckId) {
}
