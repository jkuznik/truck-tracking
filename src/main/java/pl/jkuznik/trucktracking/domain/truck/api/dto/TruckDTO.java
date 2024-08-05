package pl.jkuznik.trucktracking.domain.truck.api.dto;

import java.time.Instant;
import java.util.UUID;

public record TruckDTO(
        String trailerPlateNumber,
        UUID businessId,
        Instant startPeriod,
        Instant endPeriod,
        UUID currentTrailerBusinessId) {
}
