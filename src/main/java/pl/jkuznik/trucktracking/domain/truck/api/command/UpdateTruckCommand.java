package pl.jkuznik.trucktracking.domain.truck.api.command;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record UpdateTruckCommand(
        @NotNull Boolean isUsed,
        Instant startPeriod,
        Instant endPeriod,
        @NotNull UUID trailerId) {
}
