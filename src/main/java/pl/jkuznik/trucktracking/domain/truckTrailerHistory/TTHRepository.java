package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.jkuznik.trucktracking.domain.shared.QueryRepository;
import pl.jkuznik.trucktracking.domain.truck.Truck;

interface TTHRepository extends QueryRepository<TruckTrailerHistory, Long> {

    Page<Truck> getTruckUsedInLastMonth(Pageable pageable);
}
