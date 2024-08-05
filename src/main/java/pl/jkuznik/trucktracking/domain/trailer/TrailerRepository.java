package pl.jkuznik.trucktracking.domain.trailer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrailerRepository extends JpaRepository<Trailer, Long> {

    Optional<Trailer> findByBusinessId(UUID uuid);
    List<Trailer> findAllByStartPeriodDate(Instant startPeriodDate);
    List<Trailer> findAllByEndPeriodDate(Instant endPeriodDate);
    List<Trailer> findAllByCrossHitch(Boolean crossHitch);

    void deleteByBusinessId(UUID uuid);


}
