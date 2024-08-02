package pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto;

import java.time.Instant;

public record TruckTrailerHistoryDTO(
        Long truckId,
        Long trailerId,
        Instant startDate,
        Instant endDate) {
}
