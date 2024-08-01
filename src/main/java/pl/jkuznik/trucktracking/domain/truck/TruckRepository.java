package pl.jkuznik.trucktracking.domain.truck;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TruckRepository extends JpaRepository<Truck, Long> {

    Optional<Truck> findByBusinessId(UUID uuid);
    void deleteByBusinessId(UUID uuid);


    @Query("""
            SELECT t
            FROM Truck t
            JOIN t.history h
            WHERE (h.startPeriodDate <= :endDate AND h.endPeriodDate >= :startDate)
            """)
    List<Truck> findTrucksByDateRange(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);
}
