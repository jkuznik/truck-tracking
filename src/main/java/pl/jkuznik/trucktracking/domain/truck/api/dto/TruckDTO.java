package pl.jkuznik.trucktracking.domain.truck.api.dto;

import java.time.Instant;
import java.util.UUID;

public record TruckDTO(
        String trailerPlateNumber,
        UUID businessId,
        Boolean isUsed,
        Instant startPeriod,
        Instant endPeriod,
        Double length,
        Double width,
        Double height) {
}
