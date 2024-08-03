package pl.jkuznik.trucktracking.domain.truck.api.command;

import jakarta.validation.constraints.NotNull;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;

import java.time.Instant;
import java.util.Set;

public record UpdateTruckCommand(
        @NotNull Boolean isUsed,
        @NotNull Boolean isCrossHitch,
        @NotNull Instant startPeriod,
        @NotNull Instant endPeriod,
        Set<Trailer> trailers) {
}
