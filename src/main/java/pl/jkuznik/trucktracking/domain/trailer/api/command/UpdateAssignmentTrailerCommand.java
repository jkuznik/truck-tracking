package pl.jkuznik.trucktracking.domain.trailer.api.command;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record   UpdateAssignmentTrailerCommand(
        Optional<Boolean> isCrossHitch,
        Optional<Instant> startPeriod,
        Optional<Instant> endPeriod,
        Optional<UUID> truckId) {
}
