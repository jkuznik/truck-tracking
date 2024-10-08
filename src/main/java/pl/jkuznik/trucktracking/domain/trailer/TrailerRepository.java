package pl.jkuznik.trucktracking.domain.trailer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.jkuznik.trucktracking.domain.shared.QueryRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrailerRepository extends QueryRepository<Trailer, Long> {

    Page<Trailer> findAll(Pageable pageable);

    Optional<Trailer> findByBusinessId(UUID uuid);
    Optional<Trailer> findByRegisterPlateNumber(String registerPlateNumber);
    List<Trailer> findAllByCrossHitch(Boolean crossHitch);

    void deleteByBusinessId(UUID uuid);


}
