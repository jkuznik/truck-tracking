package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.api.dto.TruckTrailerHistoryDTO;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trailer")
@RequiredArgsConstructor
public class TrailerController {

    private final TrailerService trailerService;

    @GetMapping("/{uuid}")
    public ResponseEntity<TrailerDTO> getTrailer(@PathVariable String uuid) {
        TrailerDTO trailerDTO = trailerService.getTrailerByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(trailerDTO);
    }

    @GetMapping
    public ResponseEntity<List<TrailerDTO>> getTrailers() {
        List<TrailerDTO> trailers = trailerService.getAllTrailers();

        return ResponseEntity.ok(trailers);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TrailerDTO>> getTrailersActualState(
            @RequestParam(required = false) Optional<Instant> startDate,
            @RequestParam(required = false) Optional<Instant> endDate,
            @RequestParam(required = false) Optional<Boolean> inUse,
            @RequestParam(required = false) Optional<Boolean> crossHitch) {
        return ResponseEntity.ok(trailerService.getTrailersByFilters(startDate, endDate, inUse, crossHitch));
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

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTrailer(@PathVariable String uuid) {
        trailerService.deleteTrailerByBusinessId(UUID.fromString(uuid));
//todo dorobic warunek sprawdzajacy czy istnieje naczepa o podanym id
        return ResponseEntity.noContent().build();
    }
}
