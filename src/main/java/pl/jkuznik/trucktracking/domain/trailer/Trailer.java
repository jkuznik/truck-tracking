package pl.jkuznik.trucktracking.domain.trailer;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
public class Trailer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String trailerPlateNumber;

    @ManyToMany(mappedBy = "trailers")
    private Set<Truck> trucks;

    private boolean inUse;
    private boolean crossHitch;
    private Instant startPeriod;
    private Instant endPeriod;

    @Column(nullable = false)
    private Double length;
    @Column(nullable = false)
    private Double height;
    @Column(nullable = false)
    private Double weight;
}
