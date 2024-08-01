package pl.jkuznik.trucktracking.domain.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
    protected String registerPlateNumber;

    @Column(nullable = false, unique = true)
    protected UUID businessId;

    @Column(nullable = false)
    protected Double length;
    @Column(nullable = false)
    protected Double height;
    @Column(nullable = false)
    protected Double weight;

    protected boolean inUse;
    protected Instant startPeriod;
    protected Instant endPeriod;

    protected AbstractEntity() {}

    protected AbstractEntity(String registerPlateNumber, UUID businessId, Double length, Double height, Double weight) {
        this.registerPlateNumber = registerPlateNumber;
        this.businessId = businessId;
        this.length = length;
        this.height = height;
        this.weight = weight;
    }
}
