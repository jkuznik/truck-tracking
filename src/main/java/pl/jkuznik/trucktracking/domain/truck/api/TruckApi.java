package pl.jkuznik.trucktracking.domain.truck.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Validated
public interface TruckApi {

    TruckDTO addTruck(@Valid AddTruckCommand newTruck);

    TruckDTO getTruckByBusinessId(@NotNull UUID uuid);
    List<TruckDTO> getAllTrucks();
    List<TruckDTO> getTrucksByDateRange(Instant startDate, Instant endDate);

    @Transactional
    TruckDTO updateTruckAssignByBusinessId(@NotNull UUID uuid, @NotNull @Valid UpdateTruckCommand updateTruckCommand);

    @Transactional
    void deleteTruckByBusinessId(@NotNull UUID uuid);
}
