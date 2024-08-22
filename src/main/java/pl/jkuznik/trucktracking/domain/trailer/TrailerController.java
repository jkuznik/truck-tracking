package pl.jkuznik.trucktracking.domain.trailer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UnassignTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/trailer")
@RequiredArgsConstructor
public class TrailerController {

    // todo czy przewidziana jest możliwość edycji numeru rejestracyjnego naczepy, a jeżeli tak to czy w bazie danych
    // historia przypisań naczepy do pojazdu powinna uwzględnić poprzednią rejestrację

    private final TrailerService trailerService;

    @GetMapping
    public ResponseEntity<Page<TrailerDTO>> getTrailers(@RequestParam(required = false) Integer pageNumber,
                                                        @RequestParam(required = false) Integer pageSize) {

        return ResponseEntity.ok(trailerService.getAllTrailers(pageNumber, pageSize));
    }
    @GetMapping("/{uuid}")
    public ResponseEntity<TrailerDTO> getTrailer(@PathVariable String uuid) {
        TrailerDTO trailerByBusinessId = trailerService.getTrailerByBusinessId(UUID.fromString(uuid));

        if (trailerByBusinessId == null) {
            throw new NoSuchElementException("No trailer with business id " + uuid);
        }

        return ResponseEntity.ok(trailerByBusinessId);
    }

    @PostMapping()
    public ResponseEntity<TrailerDTO> createTrailer(@RequestBody AddTrailerCommand addTrailerCommand) {

        TrailerDTO trailerDTO = trailerService.addTrailer(addTrailerCommand);
        return ResponseEntity.status(201).body(trailerDTO);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<TrailerDTO> updateCrossHitchTrailerValue(@PathVariable String uuid, @RequestBody UpdateCrossHitchTrailerCommand updateCrossHitchTrailerCommand) {

        return ResponseEntity.ok(trailerService.updateCrossHitchTrailerValue(UUID.fromString(uuid), updateCrossHitchTrailerCommand));
    }

    @PatchMapping("/{uuid}/assign-manage")
    public ResponseEntity<TrailerDTO> assignTrailer(@PathVariable String uuid, @RequestBody UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand) {

        return ResponseEntity.status(200).body(trailerService.assignTrailerByBusinessId(UUID.fromString(uuid), updateAssignmentTrailerCommand));
    }

    @PatchMapping("/{uuid}/unassign-manage")
    public ResponseEntity<TrailerDTO> unassignTrailer(@PathVariable String uuid, @RequestBody UnassignTrailerCommand updateAssignmentTrailerCommand) {
        return ResponseEntity.status(200).body(trailerService.unassignTrailerByBusinessId(UUID.fromString(uuid), updateAssignmentTrailerCommand));
    }

    @PatchMapping("/{uuid}/cross-hitch")
    public ResponseEntity<String> crossHitchOperation(@PathVariable String uuid, @RequestBody UpdateAssignmentTrailerCommand updateAssignmentTrailerCommand) {
        TrailerDTO processingTrailer = trailerService.getTrailerByBusinessId(UUID.fromString(uuid));

        if (!processingTrailer.isCrossHitch()) {
            return ResponseEntity.badRequest().body("Trailer is not cross hitch operation available");
        }

        if (updateAssignmentTrailerCommand.truckId().isEmpty()) {
            return ResponseEntity.badRequest().body("Truck id cannot be empty in cross hitch operation");
        }

        return ResponseEntity.ok(trailerService.crossHitchOperation(UUID.fromString(uuid), updateAssignmentTrailerCommand));
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
