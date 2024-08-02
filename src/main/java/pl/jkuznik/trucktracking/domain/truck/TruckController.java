package pl.jkuznik.trucktracking.domain.truck;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/truck")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @Operation(summary = "opis summary")
//    @ApiResponses(value = {})
    @ApiResponse(responseCode = "200", description = "opis ApiResponse")
    @GetMapping
    public ResponseEntity<List<TruckDTO>> getTrucks() {
        List<TruckDTO> allTrucks = truckService.getAllTrucks();

        return ResponseEntity.status(200).body(allTrucks);
    }

    @Operation(summary = "metoda post")
    @ApiResponse(responseCode = "201", description = "metoda powinna zwrocic status 201 created, dodac ")
    @PostMapping()
    public ResponseEntity<TruckDTO> createTruck(@RequestBody AddTruckCommand requestTruck) {
        List<String> currentTrucks = truckService.getAllTrucks().stream()
                .map(TruckDTO::trailerPlateNumber)
                .toList();

        if (currentTrucks.contains(requestTruck.registerPlateNumber())) {
            throw new RuntimeException("Plate number already exists");  //TODO działa ale poprawic bo leci status 500
        }

        TruckDTO responseTruck = truckService.addTruck(requestTruck);

        //TODO dopisać generowanie adresu pod ktorym bedzie dostepny nowy zasob oraz obsłużyć wyjątki
        return ResponseEntity.status(201).body(responseTruck);
    }

    @Operation(summary = "Endpoint służący do zarządzania naczepami dla danego ciągnika oraz planowanym" +
            " okresem przypisania naczepy, pozostałe parametry ciągnika z natury powinny być finalnymi")
    @ApiResponse(responseCode = "200", description = "Zarządzanie naczepami")
    @PatchMapping("/{uuid}")
    public ResponseEntity<TruckDTO> updateTruck(@PathVariable String uuid, @RequestBody UpdateTruckCommand updateTruckCommand) {
        TruckDTO updatedTruck = truckService.updateTruckByBusinessId(UUID.fromString(uuid), updateTruckCommand);

        return ResponseEntity.status(200).body(updatedTruck);
    }
}
