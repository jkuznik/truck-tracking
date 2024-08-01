package pl.jkuznik.trucktracking.domain.trailer;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.shared.AbstractEntity;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Trailer extends AbstractEntity {

    @ManyToMany(mappedBy = "trailers")
    private Set<Truck> trucks;

    private boolean crossHitch;

    public Trailer() {}

    public Trailer(String registerPlateNumber, UUID businessId, Double length, Double height, Double weight) {
        super(registerPlateNumber, businessId, length, height, weight);
    }
}

