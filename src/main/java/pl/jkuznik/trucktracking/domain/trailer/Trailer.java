package pl.jkuznik.trucktracking.domain.trailer;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.shared.AbstractEntity;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.util.Set;

@Entity
@Getter
@Setter
public class Trailer extends AbstractEntity {

    @ManyToMany(mappedBy = "trailers")
    private Set<Truck> trucks;

    private boolean crossHitch;
}

