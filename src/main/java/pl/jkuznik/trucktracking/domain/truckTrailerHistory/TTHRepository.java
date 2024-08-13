package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface TTHRepository extends JpaRepository<TruckTrailerHistory, Long> {

    List<TruckTrailerHistory> findAllByTruckId(Long truckId);
    List<TruckTrailerHistory> findAllByTrailerId(Long trailerId);
    List<TruckTrailerHistory> findAllByStartPeriodDate(Instant startPeriodDate);
    List<TruckTrailerHistory> findAllByEndPeriodDate(Instant endPeriodDate);

}
