package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddTrailerCommand(
        @NotBlank String registerPlateNumber) {
}