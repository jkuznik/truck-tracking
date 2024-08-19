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

    private UUID currentTruckBusinessId;
    private Instant startPeriodDate;
    private Instant endPeriodDate;

    protected Trailer() {
    }

    public Trailer(UUID businessId, String registerPlateNumber) {
        super(businessId);
        this.registerPlateNumber = registerPlateNumber;
    }

    public boolean isInUse(Instant startDate, Instant endDate) throws IllegalStateException {

        // Jeżeli naczepa nie ma określonego czasu przypisania wtedy jest dostępna w każdym terminie,
        // w innym wypadku 'result' metody isInUse() '= true' oraz ewentalna dostępność naczepy wg poniższej logiki warunków
        boolean result = startPeriodDate != null || endPeriodDate != null;


        // Jeżeli nowe przypisanie określa początek i koniec przypisania
        if (startDate != null && endDate != null) {
            if (startPeriodDate != null && endPeriodDate != null) {
                if (startPeriodDate.isAfter(endDate) || endPeriodDate.isBefore(startDate)) result = false;
            }
            if (startPeriodDate != null && endPeriodDate == null) {
                if (startPeriodDate.isAfter(endDate)) result = false;
            }
            if (startPeriodDate == null && endPeriodDate != null) {
                if (endPeriodDate.isBefore(startDate)) result = false;
            }
        }

        // Jeżeli nowe przypisanie określa tylko początek przypisania
        if (startDate != null && endDate == null) {
            if (startPeriodDate != null && endPeriodDate != null) {
                if (endPeriodDate.isBefore(startDate)) result = false;
            }
            if (startPeriodDate != null && endPeriodDate == null) {
                throw new IllegalStateException("Processing trailer is currently assigned to a truck without end period. To add new assign edit first current assignment date or fill end date of new assign");
            }
            if (startPeriodDate == null && endPeriodDate != null) {
                if (endPeriodDate.isBefore(startDate)) result = false;
            }
        }

        // todo jeżeli nowe przypisanie określa tylko koniec okresu a obecnie jest przypisana naczepa to ma być rzucony
        // wyjatek czy automatycznie ma nadac nowe przypisanie z wartością początekNowego = koniecStarego?
        // na ten moment metoda sprawdzająca rzuca wyjątek konfliktu okresów przypisania
        if (startDate == null && endDate != null) {
            if (startPeriodDate != null && endPeriodDate != null) {
                if (startPeriodDate.isAfter(endDate)) result = false;
            }
            if (startPeriodDate != null && endPeriodDate == null) {
                if (startPeriodDate.isAfter(endDate)) result = false;
            }
            if (startPeriodDate == null && endPeriodDate != null) {
                throw new IllegalStateException("Processing trailer is currently assigned to a truck without start period. To add new assign edit first current assignment date or fill start date of new assign");
            }
        }

        if (startDate == null && endDate == null) result = false;
        return result;
    }
}

