package pl.jkuznik.trucktracking.domain.truck.api.command;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record UpdateTruckCommand(
        Optional<Instant> startPeriod,
        Optional<Instant> endPeriod,
        Optional<UUID> trailerId) {
}
