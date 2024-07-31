package pl.jkuznik.trucktracking.domain.shared;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
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
}
