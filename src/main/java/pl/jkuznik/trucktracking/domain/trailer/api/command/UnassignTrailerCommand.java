package pl.jkuznik.trucktracking.domain.trailer.api.command;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
public record UnassignTrailerCommand(@NotNull UUID trailerId) {
}
