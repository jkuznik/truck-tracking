package pl.jkuznik.trucktracking.domain.truck;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    // todo czy przewidziana jest możliwość edycji numeru rejestracyjnego pojazdu, a jeżeli tak to czy w bazie danych
    // historia przypisań naczepy do pojazdu powinna uwzględnić poprzednią rejestrację

    private final TruckService truckService;

    @GetMapping("/{uuid}")
    public ResponseEntity<TruckDTO> getTruck(@PathVariable String uuid) {
        TruckDTO truckDTO = truckService.getTruckByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(truckDTO);
    }

    @GetMapping
    public ResponseEntity<Page<TruckDTO>> getAllTrucks(@RequestParam(required = false) Integer pageNumber,
                                                    @RequestParam(required = false) Integer pageSize) {
        Page<TruckDTO> trucks = truckService.getAllTrucks(pageNumber, pageSize);

        return ResponseEntity.ok(trucks);
    }

    @GetMapping("/history")
    public ResponseEntity<Page<TruckDTO>> getAllTrucksUsedInLastMonth(@RequestParam(required = false) Integer pageNumber,
                                                          @RequestParam(required = false) Integer pageSize) {

        return ResponseEntity.ok(truckService.getAllTrucksUsedInLastMonth(pageNumber, pageSize));
    }


    @PostMapping()
    public ResponseEntity<TruckDTO> createTruck(@RequestBody AddTruckCommand addTruckCommand) {
        TruckDTO responseTruck = truckService.addTruck(addTruckCommand);

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
