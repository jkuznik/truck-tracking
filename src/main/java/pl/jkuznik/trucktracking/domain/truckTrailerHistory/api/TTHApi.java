package pl.jkuznik.trucktracking.domain.truckTrailerHistory.api;

import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

@Validated
public interface TTHApi {

    TruckTrailerHistory save(TruckTrailerHistory truckTrailerHistory);
}
