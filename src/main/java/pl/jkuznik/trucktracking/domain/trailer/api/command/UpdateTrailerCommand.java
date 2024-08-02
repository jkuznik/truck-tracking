package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotNull;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public record UpdateTrailerCommand(
        @NotNull Boolean isUsed,
        Boolean isCrossHitch,
        Optional<Instant> startPeriod,
        Optional<Instant> endPeriod,
        @NotNull UUID truckId) {
}
