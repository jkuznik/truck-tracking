package pl.jkuznik.trucktracking.domain.trailer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class Trailer extends AbstractEntity {

    @Column(nullable = false, unique = true)
    protected String registerPlateNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "trailer")
    private Set<TruckTrailerHistory> history = new HashSet<>();

    private boolean crossHitch;

    protected Trailer() {}

    public Trailer(String registerPlateNumber, UUID businessId, Double length, Double height, Double weight) {
        super(businessId, length, height, weight);
        this.registerPlateNumber = registerPlateNumber;
    }
}

