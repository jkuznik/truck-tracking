package pl.jkuznik.trucktracking.domain.trailer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrailerRepository extends JpaRepository<Trailer, Long> {

    Optional<Trailer> findByBusinessId(UUID uuid);
    void deleteByBusinessId(UUID uuid);

}
