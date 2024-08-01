package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotNull;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;
import java.util.Set;

public record UpdateTrailerCommand(
        @NotNull Boolean isUsed,
        @NotNull Boolean isCrossHitch,
        @NotNull Instant startPeriod,
        @NotNull Instant endPeriod,
        Set<Truck> trucks) {
}
