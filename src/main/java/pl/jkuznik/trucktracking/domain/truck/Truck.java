package pl.jkuznik.trucktracking.domain.truck;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.shared.AbstractEntity;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Truck extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String registerPlateNumber;

    @OneToMany(mappedBy = "truck")
    private Set<TruckTrailerHistory> history = new HashSet<>();

    private boolean inUse;
    private Instant startPeriodDate;
    private Instant endPeriodDate;

    protected Truck() {}

    public Truck(UUID businessId, String registerPlateNumber) {
        super(businessId);
        this.registerPlateNumber = registerPlateNumber;
    }
}
