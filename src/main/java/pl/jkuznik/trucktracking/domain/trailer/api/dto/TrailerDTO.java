package pl.jkuznik.trucktracking.domain.trailer.api.dto;

import java.time.Instant;
import java.util.UUID;

public record TrailerDTO(
        String trailerPlateNumber,
        UUID businessId,
        Boolean isUsed,
        Boolean isCorssHitch,
        Instant startPeriod,
        Instant endPeriod,
        Double length,
        Double width,
        Double height) {
}
