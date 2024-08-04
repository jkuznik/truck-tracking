package pl.jkuznik.trucktracking.domain.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    protected Long version;
    @Column(nullable = false, unique = true)
    protected UUID businessId;


    protected boolean inUse;
    protected Instant startPeriodDate;
    protected Instant endPeriodDate;

    protected AbstractEntity() {}

    protected AbstractEntity(UUID businessId, Double length, Double height, Double weight) {
        this.businessId = businessId;
        this.length = length;
        this.height = height;
        this.weight = weight;
    }
}
