package pl.jkuznik.trucktracking.domain.truck;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.shared.AbstractEntity;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Truck extends AbstractEntity {

    @ManyToMany
    @JoinTable(
            name = "truck_trailer",
            joinColumns = @JoinColumn(name = "truck_id"),
            inverseJoinColumns = @JoinColumn(name = "trailer_id")
    )
    private Set<Trailer> trailers;

    public Truck() {}

    public Truck(String registerPlateNumber, UUID businessId, Double length, Double height, Double weight) {
        super(registerPlateNumber, businessId, length, height, weight);
    }
}
