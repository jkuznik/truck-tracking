package pl.jkuznik.trucktracking.domain.truck.api.dto;

import java.time.Instant;

public record TruckDTO(
        String trailerPlateNumber,
        Boolean isUsed,
        Instant startPeriod,
        Instant endPeriod,
        Double length,
        Double width,
        Double height) {
}
