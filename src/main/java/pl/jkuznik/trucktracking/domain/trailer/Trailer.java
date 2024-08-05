package pl.jkuznik.trucktracking.domain.trailer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
public class Trailer extends AbstractEntity {

    @Column(nullable = false, unique = true)
    private String registerPlateNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "trailer")
    private Set<TruckTrailerHistory> history = new HashSet<>();

    private boolean crossHitch;

    private boolean inUse;
    private UUID currentTruckBusinessId;
    private Instant startPeriodDate;
    private Instant endPeriodDate;

    protected Trailer() {}

    public Trailer(UUID businessId, String registerPlateNumber) {
        super(businessId);
        this.registerPlateNumber = registerPlateNumber;
    }
}

