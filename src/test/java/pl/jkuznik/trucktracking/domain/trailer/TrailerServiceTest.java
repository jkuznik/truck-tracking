package pl.jkuznik.trucktracking.domain.trailer;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {TrailerService.class, MethodValidationPostProcessor.class})
class TrailerServiceTest {

    @Autowired
    TrailerApi trailerApi;

    @MockBean
    TrailerRepository trailerRepository;

    @MockBean
    TruckRepository truckRepository;

    @MockBean
    TTHRepository tthRepository;

    private final UUID trailerBusinessId = UUID.randomUUID();
    private final UUID truckBusinessId = UUID.randomUUID();
    private final String trailerRegisterNumber = "TRAILER001";
    private final String truckRegisterNumber = "TRUCK001";
    private final Instant stratPeriodDate = Instant.parse("2024-01-01T00:00:00Z");
    private final Instant endPeriodDate = Instant.parse("2024-01-02T00:00:00Z");
    private Trailer testTrailer = new Trailer();
    private Truck testTruck = new Truck(truckBusinessId, "TRUCK001");

    @BeforeEach
    void setUp() {
        //given
        testTrailer.setRegisterPlateNumber(trailerRegisterNumber);
        testTrailer.setBusinessId(truckBusinessId);
        testTrailer.setCrossHitch(true);
        testTrailer.setStartPeriodDate(stratPeriodDate);
        testTrailer.setEndPeriodDate(endPeriodDate);
        testTrailer.setCurrentTruckBusinessId(truckBusinessId);

        testTruck.setRegisterPlateNumber(truckRegisterNumber);
        testTruck.setBusinessId(truckBusinessId);
        testTruck.setStartPeriodDate(stratPeriodDate);
        testTruck.setEndPeriodDate(endPeriodDate);
        testTruck.setCurrentTrailerBusinessId(truckBusinessId);
    }

    @Nested
    class GetMethodsTests {
        @Test
        void getTrailerByBusinessIdWhenTrailerExist() {
            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));

            //then
            TrailerDTO result = trailerApi.getTrailerByBusinessId(trailerBusinessId);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
        }

        @Test
        void getTrailerByBusinessIdWhenTrailerNotExist() {
            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() -> trailerApi.getTrailerByBusinessId(trailerBusinessId));

            assertThat(exception).isInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + trailerBusinessId);
        }

        @Test
        void getAllTrailers() {
            //when
            when(trailerRepository.findAll()).thenReturn(List.of(testTrailer));

            //then
            List<TrailerDTO> trailers = trailerApi.getAllTrailers();

            assertThat(trailers.size()).isEqualTo(1);
            assertThat(trailers.getFirst().trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
        }


        //        @Test
//        void getTrailersByStartPeriodDate() {
//
//        }
//
//        @Test
//        void getTrailersByEndPeriodDate() {
//        }

//        @Test
//        void getTrailersByFilters() {
//
//        }
//
//        @Test
//        void getTrailersHistoryByFilters() {
//        }

        @Test
        void getTrailersByCrossHitch() {
            //when
            when(trailerRepository.findAllByCrossHitch(true)).thenReturn(List.of(testTrailer));

            //then
            List<TrailerDTO> trailersByCrossHitch = trailerApi.getTrailersByCrossHitch(true);

            assertThat(trailersByCrossHitch.size()).isEqualTo(1);
            assertThat(true).isEqualTo(trailersByCrossHitch.getFirst().isCrossHitch());
        }
    }

    @Nested
    class PostMethodsTests {
        @Test
        void addTrailerWhenCommandIsValidAndTrailerNotExist() {
            //given
            AddTrailerCommand addTrailerCommand = new AddTrailerCommand(trailerRegisterNumber);

            //when
            when(trailerRepository.findByRegisterPlateNumber(trailerRegisterNumber)).thenReturn(Optional.empty());
            when(trailerRepository.save(any(Trailer.class))).thenReturn(testTrailer);

            //then
            TrailerDTO newTrailerDTO = trailerApi.addTrailer(addTrailerCommand);

            verify(trailerRepository, times(1)).save(any(Trailer.class));
            assertThat(trailerRegisterNumber).isEqualTo(newTrailerDTO.trailerPlateNumber());

        }

        @Test
        void addTrailerWhenCommandIsValidAndTrailerExist() {
            //given
            AddTrailerCommand addTrailerCommand = new AddTrailerCommand(trailerRegisterNumber);

            //when
            when(trailerRepository.findByRegisterPlateNumber(trailerRegisterNumber)).thenReturn(Optional.of(testTrailer));

            //then
            var exception = catchException(() -> trailerApi.addTrailer(addTrailerCommand));

            assertThat(exception).isInstanceOf(RuntimeException.class);
            assertThat(exception.getMessage()).isEqualTo("Trailer with " + addTrailerCommand.registerPlateNumber() + " plate number already exists");
        }

        @Test
        void addTrailerWhenCommandIsBlank() {
            //given
            AddTrailerCommand addTrailerCommand = new AddTrailerCommand("");

            //when
            var exception = catchException(() -> trailerApi.addTrailer(addTrailerCommand));

            //then
            assertThat(exception).isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void addTrailerWhenCommandIsNull() {
            //when
            var exception = catchException(() -> trailerApi.addTrailer(null));

            //then
            assertThat(exception).isExactlyInstanceOf(ConstraintViolationException.class);
        }
    }

    @Nested
    class PatchMethodsTests {

        @Test
        void updateTrailerByBusinessId() {
        }

        @Test
        void assignTrailerManageByBusinessId() {
        }

        @Test
        void crossHitchOperation() {
        }
    }

    @Nested
    class DeleteMethodsTests {
        @Test
        void deleteTrailerByBusinessId() {
            //when
            doNothing().when(trailerRepository).deleteByBusinessId(any(UUID.class));

            //then
            trailerApi.deleteTrailerByBusinessId(trailerBusinessId);
            verify(trailerRepository, times(1)).deleteByBusinessId(trailerBusinessId);
        }
    }
}