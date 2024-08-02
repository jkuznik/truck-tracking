package pl.jkuznik.trucktracking.domain.truckTrailerHistory.api;

import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;

import java.time.Instant;
import java.util.List;

@Validated
public interface TTHApi {

    TruckTrailerHistory save(TruckTrailerHistory truckTrailerHistory);

    List<TruckTrailerHistoryDTO> getAllTrailersByTruckId(Long truckId);
    List<TruckTrailerHistoryDTO> getAllTrucksByTrailerId(Long truckId);
    List<TruckTrailerHistoryDTO> getTrailersByStartPeriodDate(Instant startDate);
    List<TruckTrailerHistoryDTO> getTrailersByEndPeriodDate(Instant endDate);
}
