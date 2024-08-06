package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpadeteAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trailer")
@RequiredArgsConstructor
public class TrailerController {

    // todo czy przewidziana jest możliwość edycji numeru rejestracyjnego naczepy, a jeżeli tak to czy w bazie danych

    // historia przypisań naczepy do pojazdu powinna uwzględnić poprzednią rejestrację
    private final TrailerService trailerService;

    @GetMapping
    public ResponseEntity<List<TrailerDTO>> getTrailers() {
        List<TrailerDTO> trailers = trailerService.getAllTrailers();

        return ResponseEntity.ok(trailers);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<TrailerDTO> getTrailer(@PathVariable String uuid) {
        TrailerDTO trailerDTO = trailerService.getTrailerByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(trailerDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrailerDTO>> getTrailersActualState(
            @RequestParam(required = false) Optional<Instant> startDate,
            @RequestParam(required = false) Optional<Instant> endDate,
            @RequestParam(required = false) Optional<Boolean> crossHitch) {
        return ResponseEntity.ok(trailerService.getTrailersByFilters(startDate, endDate, crossHitch));
    }

    @GetMapping("/history")
    public ResponseEntity<List<TruckTrailerHistoryDTO>> getTrailersHistory(
            @RequestParam(required = false) Optional<Instant> startDate,
            @RequestParam(required = false) Optional<Instant> endDate,
            @RequestParam(required = false) Optional<UUID> truckId,
            @RequestParam(required = false) Optional<UUID> trailerId) {
        return ResponseEntity.ok(trailerService.getTrailersHistoryByFilters(startDate, endDate, truckId, trailerId));
    }

    @PostMapping()
    public ResponseEntity<TrailerDTO> createTrailer(@RequestBody AddTrailerCommand addTrailerCommand) {
        List<String> currentTrailers = trailerService.getAllTrailers().stream()
                .map(TrailerDTO::trailerPlateNumber)
                .toList();

        if (currentTrailers.contains(addTrailerCommand.registerPlateNumber())) {
            throw new RuntimeException("Plate number already exists");  //TODO działa ale poprawic bo leci status 500
        }

        TrailerDTO responseTrailer = trailerService.addTrailer(addTrailerCommand);

        //TODO dopisać generowanie adresu pod ktorym bedzie dostepny nowy zasob oraz obsłużyć wyjątki
        return ResponseEntity.status(201).body(responseTrailer);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<TrailerDTO> updateTrailerByBusinessId(@PathVariable String uuid, @RequestBody UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand) throws Exception {

        return ResponseEntity.ok(trailerService.updateTrailerByBusinessId(UUID.fromString(uuid), updateCrossHitchTrailerCommand));
    }

    @PatchMapping("/{uuid}/assign-manage")
    public ResponseEntity<TrailerDTO> assignTrailerManage(@PathVariable String uuid, @RequestBody UpadeteAssignmentTrailerCommand upadeteAssignmentTrailerCommand) throws Exception {
        TrailerDTO updatedTrailer = trailerService.assignTrailerManageByBusinessId(UUID.fromString(uuid), upadeteAssignmentTrailerCommand);

        return ResponseEntity.status(200).body(updatedTrailer);
    }

    @PatchMapping("/{uuid}/cross-hitch")
    public ResponseEntity<String> crossHitchTrailerByBusinessId(@PathVariable String uuid, @RequestBody UpadeteAssignmentTrailerCommand upadeteAssignmentTrailerCommand) {
        TrailerDTO processingTrailer = trailerService.getTrailerByBusinessId(UUID.fromString(uuid));

        if (!processingTrailer.isCrossHitch()) {
            return ResponseEntity.badRequest().body("Trailer is not cross hitch operation available");
        }

        if (upadeteAssignmentTrailerCommand.truckId().isEmpty()) {
            return ResponseEntity.badRequest().body("Truck id cannot be empty in cross hitch operation");
        }

        var result = trailerService.crossHitchOperation(UUID.fromString(uuid), upadeteAssignmentTrailerCommand);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTrailer(@PathVariable String uuid) {

        if (trailerService.getTrailerByBusinessId(UUID.fromString(uuid)) == null) {
            throw new NoSuchElementException("Trailer with id " + uuid + " does not exist");
        }

        trailerService.deleteTrailerByBusinessId(UUID.fromString(uuid));
        return ResponseEntity.noContent().build();
    }
}
