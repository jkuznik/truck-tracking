package pl.jkuznik.trucktracking.domain.trailer.api.dto;

import java.time.Instant;

public record TrailerDTO(
        String trailerPlateNumber,
        Boolean isUsed,
        Instant startPeriod,
        Instant endPeriod,
        Double length,
        Double width,
        Double height) {
}
