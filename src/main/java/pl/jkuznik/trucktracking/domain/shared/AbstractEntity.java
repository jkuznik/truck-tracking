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

    protected AbstractEntity() {}

    protected AbstractEntity(UUID businessId) {
        this.businessId = businessId;
    }
}
