package pl.jkuznik.trucktracking.domain.truck;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<Truck, Long> {
}
