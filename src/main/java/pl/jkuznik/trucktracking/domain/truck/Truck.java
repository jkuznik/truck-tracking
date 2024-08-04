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

    @Column(nullable = false, unique = true)
    protected String registerPlateNumber;

    @OneToMany(mappedBy = "truck")
    private Set<TruckTrailerHistory> history = new HashSet<>();

    public Truck() {}

    public Truck(UUID businessId, Double length, Double height, Double weight) {
        super(businessId, length, height, weight);
        this.registerPlateNumber = UUID.randomUUID().toString();
    }
}
