package pl.jkuznik.trucktracking.domain.trailer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
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

    @Operation(summary = "Zwraca naczepę według UUID")
    @Parameter(
            name = "uuid",
            description = "UUID identyfikujący naczepę o identyfikatorze 1 w bazie danych",
            required = true,
            example = "542602cf-97d5-4548-8831-55f21d35fcf4")
    @ApiResponse(responseCode = "200")
    @GetMapping("/{uuid}")
    public ResponseEntity<TrailerDTO> getTrailer(@PathVariable String uuid) {
        TrailerDTO trailerDTO = trailerService.getTrailerByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.ok(trailerDTO);
    }

    @Operation(summary = "Zwraca listę wszystkich naczep")
    @ApiResponse(responseCode = "200")
    @GetMapping
    public ResponseEntity<List<TrailerDTO>> getTrailers() {
        List<TrailerDTO> trailers = trailerService.getAllTrailers();

        return ResponseEntity.ok(trailers);
    }

    @Operation(summary = "Zwraca listę aktualnego stanu naczep według podanych filtrów: czy aktualnie jest w użytku, czy naczepa jest oflagowana jako 'corssHitch', okres czasu w którym naczepa była użytkowana")
    @ApiResponse(responseCode = "200")
    @GetMapping("/search")
    public ResponseEntity<List<TrailerDTO>> getTrailersActualState(
            @RequestParam(required = false) Optional<Instant> startDate,
            @RequestParam(required = false) Optional<Instant> endDate,
            @RequestParam(required = false) Optional<Boolean> inUse,
            @RequestParam(required = false) Optional<Boolean> crossHitch) {
        return ResponseEntity.ok(trailerService.getTrailersByFilters(startDate, endDate, inUse, crossHitch));
    }

    @Operation(summary = "Zwraca historię naczep według podanego filtru: okres czasu w którym naczepa była użytkowana")
    @ApiResponse(responseCode = "200")
    @GetMapping("/history")
    public ResponseEntity<List<TruckTrailerHistoryDTO>> getTrailersHistory(
            @RequestParam(required = false) Optional<Instant> startDate,
            @RequestParam(required = false) Optional<Instant> endDate) {
        return ResponseEntity.ok(trailerService.getTrailersHistoryByFilters(startDate, endDate));
    }

    @Operation(summary = "Dodawanie naczepy")
    @ApiResponse(responseCode = "201")
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

//    @Operation(summary = "Endpoint służący do zarządzania naczepami dla danego ciągnika oraz planowanym" +
//            " okresem przypisania naczepy, pozostałe parametry naczepy z natury powinny być finalnymi")
//    @ApiResponse(responseCode = "200")
//    @Parameter(
//            name = "uuid",
//            description = "UUID identyfikujący naczepę o id 1 w bazie danych",
//            required = true,
//            example = "542602cf-97d5-4548-8831-55f21d35fcf4")
//    @PatchMapping("/{uuid}")
//    public ResponseEntity<TrailerDTO> updateTrailer(@PathVariable String uuid, @RequestBody UpdateTrailerCommand updateTrailerCommand) throws Exception {
//        TrailerDTO updatedTrailer = trailerService.updateTrailerByBusinessId(UUID.fromString(uuid), updateTrailerCommand);
//
//        return ResponseEntity.status(200).body(updatedTrailer);
//    }

    @Operation(summary = "Usuwanie naczepy według UUID")
    @ApiResponse(responseCode = "204")
    @Parameter(
            name = "uuid",
            description = "UUID identyfikujący naczepę o identyfikatorze 1 w bazie danych",
            required = true,
            example = "542602cf-97d5-4548-8831-55f21d35fcf4")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTrailer(@PathVariable String uuid) {
        trailerService.deleteTrailerByBusinessId(UUID.fromString(uuid));

        return ResponseEntity.noContent().build();
    }
}
