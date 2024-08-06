package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/truck")
@RequiredArgsConstructor
public class TruckController {

    private final TruckService truckService;

    @GetMapping("/{uuid}")
    public ResponseEntity<TruckDTO> getTruck(@PathVariable String uuid) {
        TruckDTO truckDTO = truckService.getTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(truckDTO);
    }

    @GetMapping
    public ResponseEntity<List<TruckDTO>> getTrucks() {
        List<TruckDTO> trucks = truckService.getAllTrucks();

        return ResponseEntity.ok(trucks);
    }

    @PostMapping()
    public ResponseEntity<TruckDTO> createTruck(@RequestBody AddTruckCommand addTruckCommand) {
        List<String> currentTrucks = truckService.getAllTrucks().stream()
                .map(TruckDTO::trailerPlateNumber)
                .toList();

        if (currentTrucks.contains(addTruckCommand.registerPlateNumber())) {
            throw new RuntimeException("Plate number already exists");  //TODO działa ale poprawic bo leci status 500
        }

        TruckDTO responseTruck = truckService.addTruck(addTruckCommand);

        //TODO dopisać generowanie adresu pod ktorym bedzie dostepny nowy zasob oraz obsłużyć wyjątki
        return ResponseEntity.status(201).body(responseTruck);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTruck(@PathVariable String uuid) {
        truckService.deleteTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.noContent().build();
    }
}
