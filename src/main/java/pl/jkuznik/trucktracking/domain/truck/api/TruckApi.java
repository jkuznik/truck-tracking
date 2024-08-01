package pl.jkuznik.trucktracking.domain.truck.api;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Validated
public interface TruckApi {

    TruckDTO addTruck(@Valid AddTruckCommand newTruck);

    TruckDTO getTruckByBusinessId(UUID uuid);
    List<TruckDTO> getAllTrucks();
    List<TruckDTO> getTrucksByDateRange(Instant startDate, Instant endDate);

    TrailerDTO updateTruckByBusinessId(UUID uuid, @Valid UpdateTruckCommand newTruck);

    void deleteTruckByBusinessId(UUID uuid);
}
