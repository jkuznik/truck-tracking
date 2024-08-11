package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/truck")
@RequiredArgsConstructor
public class TruckController {

    // todo czy przewidziana jest możliwość edycji numeru rejestracyjnego pojazdu, a jeżeli tak to czy w bazie danych
    // historia przypisań naczepy do pojazdu powinna uwzględnić poprzednią rejestrację

    private final TruckService truckService;

    @GetMapping("/{uuid}")
    public ResponseEntity<TruckDTO> getTruck(@PathVariable String uuid) {
        TruckDTO truckDTO = truckService.getTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(truckDTO);
    }

    @GetMapping
    public ResponseEntity<List<TruckDTO>> getTrucks(@RequestParam(required = false) boolean lastMonth) {
        List<TruckDTO> trucks = truckService.getAllTrucks(lastMonth);

        return ResponseEntity.ok(trucks);
    }


    @PostMapping()
    public ResponseEntity<TruckDTO> createTruck(@RequestBody AddTruckCommand addTruckCommand) {
        // TODO utowrzyć metodę getByPlateNumber i wykorzystać ją do sprawdzenia czy taki pojazd już istnieje
        List<String> currentTrucks = truckService.getAllTrucks(false).stream()
                .map(TruckDTO::trailerPlateNumber)
                .toList();

        if (currentTrucks.contains(addTruckCommand.registerPlateNumber())) {
            throw new RuntimeException("Plate number already exists");  //TODO działa ale poprawic bo leci status 500
        }

        TruckDTO responseTruck = truckService.addTruck(addTruckCommand);

        //TODO dopisać generowanie adresu pod ktorym bedzie dostepny nowy zasob oraz obsłużyć wyjątki
        return ResponseEntity.status(201).body(responseTruck);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<TruckDTO> updateTruckAssign(@PathVariable String uuid, @RequestBody UpdateTruckCommand updateTruckCommand) throws Exception {
        TruckDTO updatedTruck = truckService.updateTruckAssignByBusinessId(UUID.fromString(uuid), updateTruckCommand);

        return ResponseEntity.status(200).body(updatedTruck);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTruck(@PathVariable String uuid) {
        truckService.deleteTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.noContent().build();
    }
}
