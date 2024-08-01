package pl.jkuznik.trucktracking.domain.truck.api.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddTruckCommand(
        @NotBlank String registerPlateNumber,
        @NotNull Double length,
        @NotNull Double height,
        @NotNull Double weight) {
}
