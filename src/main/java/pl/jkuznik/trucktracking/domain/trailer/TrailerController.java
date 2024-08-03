package pl.jkuznik.trucktracking.domain.trailer;

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

    @GetMapping
    public List<TrailerDTO> getTrailers() {
        return trailerService.getAllTrailers();
    }
}
