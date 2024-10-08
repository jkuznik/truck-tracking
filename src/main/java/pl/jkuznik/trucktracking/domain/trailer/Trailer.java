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

        if (startDate == null && endDate == null) {
            throw new IllegalStateException("Both value of start date and end date can't be empty.");
        }

        if (startPeriodDate == null && endPeriodDate == null) return false;

        boolean result = true;

        String useCase = "";
        if (startDate != null && endDate != null) { useCase = "Both of new start date and new end date are present"; }
        if (endDate == null) { useCase = "New end date is empty"; }
        if (startDate == null) { useCase = "New start date is empty"; }

        switch (useCase) {
            case "Both of new start date and new end date are present" -> {
                if (startPeriodDate != null && endPeriodDate != null) {
                    if (startPeriodDate.isAfter(endDate) || endPeriodDate.isBefore(startDate)) result = false;
                }
                if (endPeriodDate == null) {
                    if (startPeriodDate.isAfter(endDate)) result = false;
                }
                if (startPeriodDate == null) {
                    if (endPeriodDate.isBefore(startDate)) result = false;
                }
            }

            case "New end date is empty" -> {
                if (startPeriodDate != null && endPeriodDate != null) {
                    if (endPeriodDate.isBefore(startDate)) result = false;
                }
                if (endPeriodDate == null) {
                    throw new IllegalStateException("Processing trailer is currently assigned to a truck without end period. To add new assign edit first current assignment date or fill end date of new assign");
                }
                if (startPeriodDate == null) {
                    if (endPeriodDate.isBefore(startDate)) result = false;
                }
            }

            case "New start date is empty" -> {
                if (startPeriodDate != null && endPeriodDate != null) {
                    if (startPeriodDate.isAfter(endDate)) result = false;
                }
                if (endPeriodDate == null) {
                    if (startPeriodDate.isAfter(endDate)) result = false;
                }
                if (startPeriodDate == null) {
                    throw new IllegalStateException("Processing trailer is currently assigned to a truck without start period. To add new assign edit first current assignment date or fill start date of new assign");
                }
            }
        }

        return result;
    }
}

