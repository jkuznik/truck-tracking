package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotBlank;

public record AddTrailerCommand(
        @NotBlank String registerPlateNumber) {
}