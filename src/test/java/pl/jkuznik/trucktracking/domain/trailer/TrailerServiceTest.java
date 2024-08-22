package pl.jkuznik.trucktracking.domain.trailer;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import pl.jkuznik.trucktracking.domain.shared.PlateNumberExistException;
import pl.jkuznik.trucktracking.domain.trailer.api.TrailerApi;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UnassignTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepositoryImpl;

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
    TTHRepositoryImpl tthRepository;

    private final String TRAILER_REGISTER_NUMBER = "TRAILER001";
    private final String TRAILER_CROSS_HITCH_REGISTER_NUMBER = "TRAILER002";
    private final String TRUCK_REGISTER_NUMBER = "TRUCK001";
    private final String TRUCK_CROSS_HITCH_REGISTER_NUMBER = "TRUCK002";
    private final UUID TRAILER_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRAILER_CROSS_HITCH_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRUCK_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRUCK_CROSS_HITCH_BUSINESS_ID = UUID.randomUUID();
    private final Instant START_PERIOD_TIME = Instant.parse("2024-01-01T00:00:00Z");
    private final Instant START_PERIOD_TIME2 = Instant.parse("2024-01-03T00:00:00Z");
    private final Instant END_PERIOD_TIME = Instant.parse("2024-01-02T00:00:00Z");
    private final Instant END_PERIOD_TIME2 = Instant.parse("2024-01-04T00:00:00Z");
    private Trailer testTrailer = new Trailer(TRUCK_BUSINESS_ID, TRAILER_REGISTER_NUMBER);
    private Truck testTruck = new Truck(TRUCK_BUSINESS_ID, TRUCK_REGISTER_NUMBER);
    private Trailer crossHitchTrailer = new Trailer(TRAILER_CROSS_HITCH_BUSINESS_ID, TRAILER_CROSS_HITCH_REGISTER_NUMBER);
    private Truck crossHitchTruck = new Truck(TRUCK_CROSS_HITCH_BUSINESS_ID, TRUCK_CROSS_HITCH_REGISTER_NUMBER);

    @BeforeEach
    void setUp() {
        //given
        testTrailer.setCrossHitch(true);
        testTrailer.setStartPeriodDate(START_PERIOD_TIME);
        testTrailer.setEndPeriodDate(END_PERIOD_TIME);
        testTrailer.setCurrentTruckBusinessId(TRUCK_BUSINESS_ID);

        testTruck.setStartPeriodDate(START_PERIOD_TIME);
        testTruck.setEndPeriodDate(END_PERIOD_TIME);
        testTruck.setCurrentTrailerBusinessId(TRUCK_BUSINESS_ID);

        crossHitchTrailer.setCrossHitch(true);
        crossHitchTrailer.setStartPeriodDate(START_PERIOD_TIME2);
        crossHitchTrailer.setEndPeriodDate(END_PERIOD_TIME2);
        crossHitchTrailer.setCurrentTruckBusinessId(TRUCK_CROSS_HITCH_BUSINESS_ID);

        crossHitchTruck.setStartPeriodDate(START_PERIOD_TIME2);
        crossHitchTruck.setEndPeriodDate(END_PERIOD_TIME2);
        crossHitchTruck.setCurrentTrailerBusinessId(TRAILER_CROSS_HITCH_BUSINESS_ID);
    }

    @Nested
    class GetMethodsTests {
        @Test
        void getTrailerByBusinessIdWhenTrailerExist() {
            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));

            //then
            TrailerDTO result = trailerApi.getTrailerByBusinessId(TRAILER_BUSINESS_ID);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
        }

        @Test
        void getTrailerByBusinessIdWhenTrailerNotExist() {
            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() -> trailerApi.getTrailerByBusinessId(TRAILER_BUSINESS_ID));

            assertThat(exception).isInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + TRAILER_BUSINESS_ID);
        }

        @Test
        void getAllTrailers() {
            PageImpl<Trailer> trailerPage = new PageImpl<>(List.of(testTrailer));

            //when
            when(trailerRepository.findAll(PageRequest.of(0,25))).thenReturn(trailerPage);

            //then
            Page<TrailerDTO> trailers = trailerApi.getAllTrailers(1, 25);

            assertThat(trailers.getContent().size()).isEqualTo(1);
            assertThat(trailers.getContent().getFirst().trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
        }

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
            AddTrailerCommand addTrailerCommand = new AddTrailerCommand(TRAILER_REGISTER_NUMBER);

            //when
            when(trailerRepository.findByRegisterPlateNumber(TRAILER_REGISTER_NUMBER)).thenReturn(Optional.empty());
            when(trailerRepository.save(any(Trailer.class))).thenReturn(testTrailer);

            //then
            TrailerDTO newTrailerDTO = trailerApi.addTrailer(addTrailerCommand);

            verify(trailerRepository, times(1)).save(any(Trailer.class));
            assertThat(TRAILER_REGISTER_NUMBER).isEqualTo(newTrailerDTO.trailerPlateNumber());

        }

        @Test
        void addTrailerWhenCommandIsValidAndTrailerExist() {
            //given
            AddTrailerCommand addTrailerCommand = new AddTrailerCommand(TRAILER_REGISTER_NUMBER);

            //when
            when(trailerRepository.findByRegisterPlateNumber(TRAILER_REGISTER_NUMBER)).thenReturn(Optional.of(testTrailer));

            //then
            var exception = catchException(() -> trailerApi.addTrailer(addTrailerCommand));

            assertThat(exception).isInstanceOf(PlateNumberExistException.class);
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
        void updateCrossHitchTrailerByBusinessIdWhenTrailerExist() {
            //given
            var updateCommand = new UpdateCrossHitchTrailerCommand(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));

            //then
            TrailerDTO trailerDTO = trailerApi.updateCrossHitchTrailerValue(TRAILER_BUSINESS_ID, updateCommand);

            assertThat(trailerDTO.isCrossHitch()).isFalse();
        }

        @Test
        void updateCrossHitchTrailerByBusinessIdWhenTrailerNotExist() {
            //given
            var updateCommand = new UpdateCrossHitchTrailerCommand(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.updateCrossHitchTrailerValue(TRAILER_BUSINESS_ID, updateCommand));

            assertThat(exception).isInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + TRAILER_BUSINESS_ID);
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndTrailerExistAndNewTimePeriodDoesNotConflict() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            Instant newStartPeriodTime = Instant.parse("2024-01-03T00:00:00Z");
            Instant newEndPeriodTime = Instant.parse("2024-01-04T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            TrailerDTO result = trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isEqualTo(newStartPeriodTime);
            assertThat(result.endPeriod()).isEqualTo(newEndPeriodTime);
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);

            verify(trailerRepository, times(1)).save(any(Trailer.class));
            verify(truckRepository, times(1)).save(any(Truck.class));
            verify(tthRepository, times(1)).save(any(TruckTrailerHistory.class));
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict2() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            // the current assignment period is defined by the start and end dates of the assignment but new value
            // has no defined start date and new end date is before current assignment what should not generate conflict
            Instant newEndPeriodTime = Instant.parse("2023-01-01T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.empty(),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var result = trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isNull();
            assertThat(result.endPeriod()).isEqualTo(newEndPeriodTime);
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict3() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            Instant newStartPeriodTime = Instant.parse("2024-01-03T00:00:00Z");
            // the current assignment period is defined by the start and end dates of the assignment but new value
            // has no defined end date and new start date is after current assignment what should not generate conflict
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.empty(), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var result = trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isEqualTo(newStartPeriodTime);
            assertThat(result.endPeriod()).isNull();
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict4() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            testTrailer.setStartPeriodDate(null);
            Instant newStartPeriodTime = Instant.parse("2024-01-03T00:00:00Z");
            // the current assignment period is defined by the end but not by the start date of the assignment and new value
            // has no defined end date and new start date is after current assignment what should not generate conflict
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.empty(), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var result = trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isEqualTo(newStartPeriodTime);
            assertThat(result.endPeriod()).isNull();
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict5() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            testTrailer.setEndPeriodDate(null);
            Instant newEndPeriodTime = Instant.parse("2023-01-01T00:00:00Z");
            // the current assignment period is defined by the start but not by the end date of the assignment and new value
            // has no defined start date and new end date is before current assignment what should not generate conflict
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.empty(),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var result = trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isNull();
            assertThat(result.endPeriod()).isEqualTo(newEndPeriodTime);
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTruckIdIsEmpty() {
            //given
            var newCrossHitch = false;
            Instant newStartPeriodTime = Instant.parse("2024-01-03T00:00:00Z");
            Instant newEndPeriodTime = Instant.parse("2024-01-04T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.of(newEndPeriodTime), Optional.empty());

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() ->
                    trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("Truck business id is needed in this operation");
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodIsEmptyButNewTruckIdIsPresent() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.empty(),
                    Optional.empty(), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() ->
                    trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Both value of start date and end date can't be empty.");
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewStartDateIsAfterEndDate() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            Instant newStartPeriodTime = Instant.parse("2024-01-05T00:00:00Z");
            Instant newEndPeriodTime = Instant.parse("2024-01-04T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() ->
                    trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("End period is before start period");

        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndTrailerNotExist() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            Instant newStartPeriodTime = Instant.parse("2024-01-03T00:00:00Z");
            Instant newEndPeriodTime = Instant.parse("2024-01-04T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + TRAILER_BUSINESS_ID);
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            // current end of assignment period time is after new start of period time what should generate conflict
            Instant newStartPeriodTime = Instant.parse("2024-01-01T00:00:00Z");
            Instant newEndPeriodTime = Instant.parse("2024-01-04T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() ->
                    trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("The trailer is in use during the specified period.");
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict2() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            // the current assignment period is defined by the start and end dates of the assignment but new value
            // has no defined start date and new end date is after current assignment what should generate conflict
            Instant newEndPeriodTime = Instant.parse("2024-01-01T00:00:00Z");
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.empty(),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() ->
                    trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("The trailer is in use during the specified period.");
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict3() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            Instant newStartPeriodTime = Instant.parse("2023-01-01T00:00:00Z");
            // the current assignment period is defined by the start and end dates of the assignment but new value
            // has no defined end date and new start date is before current assignment what should generate conflict
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.empty(), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() -> trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("The trailer is in use during the specified period.");
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict4() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            testTrailer.setEndPeriodDate(null);
            Instant newStartPeriodTime = Instant.parse("2023-01-01T00:00:00Z");
            // the current assignment period is defined by the start but not by end date of the assignment and new value
            // has no defined start date but not by end date and new start date is after current assignment what should generate conflict
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.of(newStartPeriodTime),
                    Optional.empty(), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() -> trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Processing trailer is currently assigned to a truck without end period. To add new assign edit first current assignment date or fill end date of new assign");
        }

        @Test
        void assignTrailerByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict5() {
            //given
            var newCrossHitch = false;
            var newTruckBusinessId = UUID.randomUUID();
            testTrailer.setStartPeriodDate(null);
            Instant newEndPeriodTime = Instant.parse("2023-01-01T00:00:00Z");
            // the current assignment period is defined by the end but not by star date of the assignment and new value
            // has no defined end date but not by staar date and new end date is before current assignment what should generate conflict
            var updateTrailerCommand = new UpdateAssignmentTrailerCommand(
                    Optional.of(newCrossHitch), Optional.empty(),
                    Optional.of(newEndPeriodTime), Optional.of(newTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() -> trailerApi.assignTrailerByBusinessId(TRAILER_BUSINESS_ID, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Processing trailer is currently assigned to a truck without start period. To add new assign edit first current assignment date or fill start date of new assign");
        }

        @Test
        void unassignTrailerByBusinessIdWhenCommandIsValidAndTruckIsAssigned() {
            //given
            var unassignCommand = new UnassignTrailerCommand(TRAILER_BUSINESS_ID, true);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            TrailerDTO trailerDTO = trailerApi.unassignTrailerByBusinessId(TRAILER_BUSINESS_ID, unassignCommand);

            assertThat(trailerDTO.startPeriod()).isNull();
            assertThat(trailerDTO.endPeriod()).isNull();
            assertThat(trailerDTO.currentTruckBusinessId()).isNull();

            assertThat(testTruck.getStartPeriodDate()).isNull();
            assertThat(testTruck.getEndPeriodDate()).isNull();
            assertThat(testTruck.getCurrentTrailerBusinessId()).isNull();
        }

        @Test
        void unassignTrailerByBusinessIdWhenCommandIsValidAndTruckIsUnassigned() {
            //given
            var unassignCommand = new UnassignTrailerCommand(TRAILER_BUSINESS_ID, true);
            testTrailer.setCurrentTruckBusinessId(null);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));

            //then
            var exception = catchException(() ->
                    trailerApi.unassignTrailerByBusinessId(TRAILER_BUSINESS_ID, unassignCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Current trailer is already unassigned");
        }

        @Test
        void unassignTrailerByBusinessIdWhenCommandIsValidAndTrailerNotExist() {
            //given
            var unassignCommand = new UnassignTrailerCommand(TRAILER_BUSINESS_ID, true);

            //when
            when(trailerRepository.findByBusinessId(TRAILER_BUSINESS_ID)).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.unassignTrailerByBusinessId(TRAILER_BUSINESS_ID, unassignCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + TRAILER_BUSINESS_ID);
        }

        @Test
        void unassignTrailerByBusinessIdWhenCommandIsValidAndTrailerExistButAssignedTruckNotExist() {
            //given
            var unassignCommand = new UnassignTrailerCommand(TRAILER_BUSINESS_ID, false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            TrailerDTO trailerDTO = trailerApi.unassignTrailerByBusinessId(TRAILER_BUSINESS_ID, unassignCommand);

            assertThat(trailerDTO.startPeriod()).isNull();
            assertThat(trailerDTO.endPeriod()).isNull();
            assertThat(trailerDTO.currentTruckBusinessId()).isNull();
        }

        @Test
        void unassignTrailerByBusinessIdWhenCommandIsNotValid() {
            //given
            var unassignCommand = new UnassignTrailerCommand(TRAILER_BUSINESS_ID, true);

            //when
            when(trailerRepository.findByBusinessId(TRAILER_BUSINESS_ID)).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.unassignTrailerByBusinessId(TRAILER_BUSINESS_ID, unassignCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No truck with business id " + testTrailer.getCurrentTruckBusinessId() +
                    "Consider to switch 'isTruckStillExist' as false");
        }

        @Test
        void crossHitchOperationWhenCommandIsValidAndTruckExistAndSecondTrailerIsCrossHitchAvailable() {
            //given
            var newStartDate = Instant.parse("2024-01-03T00:00:00Z");
            var newEndDate = Instant.parse("2024-01-04T00:00:00Z");
            var updateAssignment = new UpdateAssignmentTrailerCommand(
                    Optional.of(true), Optional.of(newStartDate),
                    Optional.of(newEndDate), Optional.of(TRUCK_CROSS_HITCH_BUSINESS_ID));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTrailer))
                    .thenReturn(Optional.of(crossHitchTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTruck))
                    .thenReturn(Optional.of(crossHitchTruck));

            //then
            String result = trailerApi.crossHitchOperation(TRAILER_BUSINESS_ID, updateAssignment);

            assertThat(testTrailer.getCurrentTruckBusinessId()).isEqualTo(TRUCK_CROSS_HITCH_BUSINESS_ID);
            assertThat(testTrailer.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(testTrailer.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTruck.getCurrentTrailerBusinessId()).isEqualTo(TRAILER_BUSINESS_ID);
            assertThat(crossHitchTruck.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(crossHitchTruck.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTrailer.getCurrentTruckBusinessId()).isEqualTo(testTruck.getBusinessId());
            assertThat(crossHitchTrailer.getStartPeriodDate()).isEqualTo(START_PERIOD_TIME2);
            assertThat(crossHitchTrailer.getEndPeriodDate()).isEqualTo(END_PERIOD_TIME2);

            assertThat(testTruck.getCurrentTrailerBusinessId()).isEqualTo(crossHitchTrailer.getBusinessId());
            assertThat(testTruck.getStartPeriodDate()).isEqualTo(START_PERIOD_TIME2);
            assertThat(testTruck.getEndPeriodDate()).isEqualTo(END_PERIOD_TIME2);
            assertThat(result).isEqualTo("Cross hitch operation on processing trailer success. ");
        }

        @Test
        void crossHitchOperationWhenCommandIsValidAndTruckExistAndSecondTrailerIsCrossHitchNotAvailable() {
            //given
            var newStartDate = Instant.parse("2024-01-03T00:00:00Z");
            var newEndDate = Instant.parse("2024-01-04T00:00:00Z");
            var updateAssignment = new UpdateAssignmentTrailerCommand(
                    Optional.of(true), Optional.of(newStartDate),
                    Optional.of(newEndDate), Optional.of(TRUCK_CROSS_HITCH_BUSINESS_ID));
            crossHitchTrailer.setCrossHitch(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTrailer))
                    .thenReturn(Optional.of(crossHitchTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTruck))
                    .thenReturn(Optional.of(crossHitchTruck));


            //then
            String result = trailerApi.crossHitchOperation(TRAILER_BUSINESS_ID, updateAssignment);

            assertThat(testTrailer.getCurrentTruckBusinessId()).isEqualTo(TRUCK_CROSS_HITCH_BUSINESS_ID);
            assertThat(testTrailer.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(testTrailer.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTruck.getCurrentTrailerBusinessId()).isEqualTo(TRAILER_BUSINESS_ID);
            assertThat(crossHitchTruck.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(crossHitchTruck.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTrailer.getCurrentTruckBusinessId()).isNull();
            assertThat(crossHitchTrailer.getStartPeriodDate()).isNull();
            assertThat(crossHitchTrailer.getEndPeriodDate()).isNull();

            assertThat(testTruck.getCurrentTrailerBusinessId()).isNull();
            assertThat(testTruck.getStartPeriodDate()).isNull();
            assertThat(testTruck.getEndPeriodDate()).isNull();

            assertThat(result).isEqualTo("Cross hitch operation on processing trailer success. " +
                    "Second trailer is not cross hitch available and will be unassigned from any truck - " +
                    "truck assignment to processing trailer before cross hitch operation now will be unassigned to any trailer");
        }

        @Test
        void crossHitchOperationWhenCommandIsValidAndTruckExistAndSecondTruckHasNoAssignTrailer() {
            //given
            var newStartDate = Instant.parse("2024-01-03T00:00:00Z");
            var newEndDate = Instant.parse("2024-01-04T00:00:00Z");
            var updateAssignment = new UpdateAssignmentTrailerCommand(
                    Optional.of(true), Optional.of(newStartDate),
                    Optional.of(newEndDate), Optional.of(TRUCK_CROSS_HITCH_BUSINESS_ID));
            crossHitchTrailer.setCrossHitch(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTrailer))
                    .thenReturn(Optional.empty());
            when(truckRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTruck))
                    .thenReturn(Optional.of(crossHitchTruck));


            //then
            String result = trailerApi.crossHitchOperation(TRAILER_BUSINESS_ID, updateAssignment);

            assertThat(testTrailer.getCurrentTruckBusinessId()).isEqualTo(TRUCK_CROSS_HITCH_BUSINESS_ID);
            assertThat(testTrailer.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(testTrailer.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTruck.getCurrentTrailerBusinessId()).isEqualTo(TRAILER_BUSINESS_ID);
            assertThat(crossHitchTruck.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(crossHitchTruck.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(testTruck.getCurrentTrailerBusinessId()).isNull();
            assertThat(testTruck.getStartPeriodDate()).isNull();
            assertThat(testTruck.getEndPeriodDate()).isNull();

            assertThat(result).isEqualTo("Cross hitch operation on processing trailer success. " +
                    "Second truck has no assignment trailer, proccessing trailer current truck now will be unassigned to any trailer.");
        }
    }

    @Nested
    class DeleteMethodsTests {
        @Test
        void deleteTrailerByBusinessId() {

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            doNothing().when(trailerRepository).deleteByBusinessId(any(UUID.class));

            //then
            trailerApi.deleteTrailerByBusinessId(TRAILER_BUSINESS_ID);
            verify(trailerRepository, times(1)).deleteByBusinessId(TRAILER_BUSINESS_ID);
        }
    }
}