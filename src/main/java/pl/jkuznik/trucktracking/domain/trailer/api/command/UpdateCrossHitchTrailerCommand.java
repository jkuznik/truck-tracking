package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

@Validated
public record UpdateCrossHitchTrailerCommand(@NotNull Boolean crossHitch) {
}
