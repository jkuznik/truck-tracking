package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.truck.Truck;

import java.time.Instant;

@Entity
@Getter
@Setter
public class TruckTrailerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "truck_id")
    private Truck truck;

    @ManyToOne
    @JoinColumn(name = "trailer_id")
    private Trailer trailer;

    @Column(name = "start_period_date")
    private Instant startPeriodDate;

    @Column(name = "end_period_date")
    private Instant endPeriodDate;

}
