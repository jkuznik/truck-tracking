package pl.jkuznik.trucktracking.domain.truck;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Zwraca pojazd według UUID")
    @Parameter(
            name = "uuid",
            description = "UUID identyfikujący ciągnik o id 1 w bazie danych",
            required = true,
            example = "52333a07-520e-465f-a6c2-5891080637e5")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{uuid}")
    public ResponseEntity<TruckDTO> getTruck(@PathVariable String uuid) {
        TruckDTO truckDTO = truckService.getTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(truckDTO);
    }

    @Operation(summary = "Zwraca listę wszystkich pojazdów")
    @ApiResponse(responseCode = "200")
    @GetMapping
    public ResponseEntity<List<TruckDTO>> getTrucks() {
        List<TruckDTO> allTrucks = truckService.getAllTrucks();

        return ResponseEntity.ok(allTrucks);
    }

    @Operation(summary = "Dodawanie pojazdu")
    @ApiResponse(responseCode = "201")
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
    @ApiResponse(responseCode = "200")
    @Parameter(
            name = "uuid",
            description = "UUID identyfikujący ciągnik o id 1 w bazie danych",
            required = true,
            example = "52333a07-520e-465f-a6c2-5891080637e5")
    @PatchMapping("/{uuid}")
    public ResponseEntity<TruckDTO> updateTruck(@PathVariable String uuid, @RequestBody UpdateTruckCommand updateTruckCommand) throws Exception {
        TruckDTO updatedTruck = truckService.updateTruckByBusinessId(UUID.fromString(uuid), updateTruckCommand);

        return ResponseEntity.status(200).body(updatedTruck);
    }

    @Operation(summary = "Usuwanie pojazdu")
    @ApiResponse(responseCode = "204")
    @Parameter(
            name = "uuid",
            description = "UUID identyfikujący ciągnik o id 1 w bazie danych",
            required = true,
            example = "52333a07-520e-465f-a6c2-5891080637e5")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTruck(@PathVariable String uuid) {
        truckService.deleteTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.noContent().build();
    }
}
