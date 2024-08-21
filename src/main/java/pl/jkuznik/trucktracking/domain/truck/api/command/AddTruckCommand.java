package pl.jkuznik.trucktracking.domain.truck.api.command;

import jakarta.validation.constraints.NotBlank;

public record AddTruckCommand(
        @NotBlank String registerPlateNumber) {
}
