package pl.jkuznik.trucktracking.domain.truck;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TruckRepository extends JpaRepository<Truck, Long> {

    Optional<Truck> findByBusinessId(UUID uuid);
    void deleteByBusinessId(UUID uuid);

    List<Truck> findByStartPeriodDateBeforeOrEndPeriodDateAfter(Instant date);
    List<Truck> findByEndPeriodDateAfter(Instant date);
}
