package pl.jkuznik.trucktracking.domain.trailer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;

import java.util.List;

@RestController
@RequestMapping("/trailer")
public class TrailerController {

    private final TrailerService trailerService;

    public TrailerController(TrailerService trailerService) {
        this.trailerService = trailerService;
    }

    @Operation(summary = "Metoda GET - zwraca listę wszystkich naczep: List<TrailerDTO> ")
    @ApiResponse(responseCode = "200", description = "opis metody")
    @GetMapping
    public List<TrailerDTO> getTrailers() {
        return trailerService.getAllTrailers();
    }
}
