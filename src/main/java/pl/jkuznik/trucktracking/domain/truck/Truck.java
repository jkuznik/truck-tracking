package pl.jkuznik.trucktracking.domain.truck;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String truckPlateNumber;

    @ManyToMany
    @JoinTable(
            name = "truck_trailer",
            joinColumns = @JoinColumn(name = "truck_id"),
            inverseJoinColumns = @JoinColumn(name = "trailer_id")
    )
    private Set<Trailer> trailers;

    private boolean inUse;
    private Instant startPeriod;
    private Instant endPeriod;

    @Column(nullable = false)
    private Double length;
    @Column(nullable = false)
    private Double height;
    @Column(nullable = false)
    private Double weight;
}
