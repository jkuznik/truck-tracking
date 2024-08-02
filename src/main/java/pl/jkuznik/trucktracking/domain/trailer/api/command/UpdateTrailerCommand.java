package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotNull;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UpdateTrailerCommand(
        @NotNull Boolean isUsed,
        Boolean isCrossHitch,
        Instant startPeriod,
        Instant endPeriod,
        @NotNull UUID truckId) {
}
