package pl.jkuznik.trucktracking.domain.truck;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.jkuznik.trucktracking.domain.shared.QueryRepository;

import java.util.Optional;
import java.util.UUID;

public interface TruckRepository extends QueryRepository<Truck, Long> {

    Page<Truck> findAll(Pageable pageable);

    Optional<Truck> findByRegisterPlateNumber(String plateNumber);
    Optional<Truck> findByBusinessId(UUID uuid);
    void deleteByBusinessId(UUID uuid);
}
