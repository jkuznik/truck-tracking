package pl.jkuznik.trucktracking.domain.trailer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.jkuznik.trucktracking.domain.bootstrap.Bootstrap;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringBootTest
class TrailerIntegrationTest {

    @Autowired
    TrailerApi trailerApi;

    @Autowired
    TrailerController trailerController;

    @Autowired
    TrailerRepository trailerRepository;

    @Autowired
    Bootstrap bootstrap;
    @Autowired
    private TruckRepository truckRepository;

    @BeforeEach
    void setUp() {
        bootstrap.run();
    }

    @Test
    void addTrailer() {
        //given
        int sizeBeforeCreation = trailerRepository.findAll().size();
        var addTrailerCommand = new AddTrailerCommand("test");

        //when
        TrailerDTO createdTrailer = trailerController.createTrailer(addTrailerCommand).getBody();

        //then
        int sizeAfterCreation = trailerRepository.findAll().size();

        assertThat(sizeBeforeCreation).isEqualTo(sizeAfterCreation - 1);
        assertThat("test").isEqualTo(createdTrailer.trailerPlateNumber());
    }

    @Test
    void getTrailer() {
        //given
        Trailer trailerById = trailerRepository.findById(1L).orElseThrow();

        //when
        TrailerDTO firstTrailerInDb = trailerController.getTrailer("a42c8aab-a60f-4a77-991e-d97c6248b33f").getBody();

        //then
        assertThat(firstTrailerInDb).isNotNull();
        assertThat(firstTrailerInDb.trailerPlateNumber()).isEqualTo(trailerById.getRegisterPlateNumber());
    }

    @Test
    void updateTrailer() {
        //given
        Trailer trailerById = trailerRepository.findById(1L).orElseThrow();
        Truck truckById = truckRepository.findById(1L).orElseThrow();

        var newStartPeriodDate = Instant.now().plusSeconds(3600);
        var updateTrailerCommand = new UpdateAssignmentTrailerCommand(Optional.empty(), Optional.of(newStartPeriodDate) ,Optional.empty(), Optional.of(truckById.getBusinessId()));

        //when
        trailerController.assignTrailer(trailerById.getBusinessId().toString(), updateTrailerCommand);

        //then
        Optional<Trailer> trailerAfterUpdateByBusinessId = trailerRepository.findByBusinessId(trailerById.getBusinessId());
        trailerAfterUpdateByBusinessId.orElseThrow();

        assertThat(trailerAfterUpdateByBusinessId.get().getStartPeriodDate()).isEqualTo(newStartPeriodDate);
    }

    @Test
    void deleteTrailer() {
        //given
        Trailer trailerById = trailerRepository.findById(1L).orElseThrow();
        List<Trailer> trailersAmountBeforeDeleteOperation = trailerRepository.findAll();

        //when
        ResponseEntity<Void> voidResponseEntity = trailerController.deleteTrailer(trailerById.getBusinessId().toString());
        var exception = catchException(() -> trailerController.getTrailer(trailerById.getBusinessId().toString()));

        //then
        List<Trailer> trailersAmountAfterDeleteOperation = trailerRepository.findAll();
        assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(trailersAmountBeforeDeleteOperation.size()).isEqualTo(trailersAmountAfterDeleteOperation.size() + 1);
        assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
    }
}