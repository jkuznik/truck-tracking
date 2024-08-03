package pl.jkuznik.trucktracking.domain.truck;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.shared.AbstractEntity;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Truck extends AbstractEntity {

    @OneToMany(mappedBy = "truck")
    private Set<TruckTrailerHistory> history = new HashSet<>();

    public Truck() {}

    public Truck(String registerPlateNumber, UUID businessId, Double length, Double height, Double weight) {
        super(registerPlateNumber, businessId, length, height, weight);
    }
}
