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
import pl.jkuznik.trucktracking.domain.trailer.api.command.UnassignTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateAssignmentTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.command.UpdateCrossHitchTrailerCommand;
import pl.jkuznik.trucktracking.domain.trailer.api.dto.TrailerDTO;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

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
    private final UUID crossHitchTrailerBusinessId = UUID.randomUUID();
    private final UUID truckBusinessId = UUID.randomUUID();
    private final UUID crossHitchTruckBusinessId = UUID.randomUUID();
    private final String trailerRegisterNumber = "TRAILER001";
    private final String crossHitchTrailerRegisterNumber = "TRAILER002";
    private final String truckRegisterNumber = "TRUCK001";
    private final String crossHitchTruckRegisterNumber = "TRUCK002";
    private final Instant startPeriodDate = Instant.parse("2024-01-01T00:00:00Z");
    private final Instant startPeriodDate2 = Instant.parse("2024-01-03T00:00:00Z");
    private final Instant endPeriodDate = Instant.parse("2024-01-02T00:00:00Z");
    private final Instant endPeriodDate2 = Instant.parse("2024-01-04T00:00:00Z");
    private Trailer testTrailer = new Trailer(truckBusinessId, trailerRegisterNumber);
    private Truck testTruck = new Truck(truckBusinessId, truckRegisterNumber);
    private Trailer crossHitchTrailer = new Trailer(crossHitchTrailerBusinessId, crossHitchTrailerRegisterNumber);
    private Truck crossHitchTruck = new Truck(crossHitchTruckBusinessId, crossHitchTruckRegisterNumber);

    @BeforeEach
    void setUp() {
        //given
        testTrailer.setCrossHitch(true);
        testTrailer.setStartPeriodDate(startPeriodDate);
        testTrailer.setEndPeriodDate(endPeriodDate);
        testTrailer.setCurrentTruckBusinessId(truckBusinessId);

        testTruck.setStartPeriodDate(startPeriodDate);
        testTruck.setEndPeriodDate(endPeriodDate);
        testTruck.setCurrentTrailerBusinessId(truckBusinessId);

        crossHitchTrailer.setCrossHitch(true);
        crossHitchTrailer.setStartPeriodDate(startPeriodDate2);
        crossHitchTrailer.setEndPeriodDate(endPeriodDate2);
        crossHitchTrailer.setCurrentTruckBusinessId(crossHitchTruckBusinessId);

        crossHitchTruck.setStartPeriodDate(startPeriodDate2);
        crossHitchTruck.setEndPeriodDate(endPeriodDate2);
        crossHitchTruck.setCurrentTrailerBusinessId(crossHitchTrailerBusinessId);
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
        void updateTrailerManageByBusinessIdWhenTrailerExist() {
            //given
            var updateCommand = new UpdateCrossHitchTrailerCommand(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));

            //then
            TrailerDTO trailerDTO = trailerApi.updateTrailerByBusinessId(trailerBusinessId, updateCommand);

            assertThat(trailerDTO.isCrossHitch()).isFalse();
        }

        @Test
        void updateTrailerManageByBusinessIdWhenTrailerNotExist() {
            //given
            var updateCommand = new UpdateCrossHitchTrailerCommand(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.updateTrailerByBusinessId(trailerBusinessId, updateCommand));

            assertThat(exception).isInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + trailerBusinessId);
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndTrailerExistAndNewTimePeriodDoesNotConflict() {
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
            TrailerDTO result = trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand);

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
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict2() {
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
            var result = trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isNull();
            assertThat(result.endPeriod()).isEqualTo(newEndPeriodTime);
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict3() {
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
            var result = trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isEqualTo(newStartPeriodTime);
            assertThat(result.endPeriod()).isNull();
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict4() {
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
            var result = trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isEqualTo(newStartPeriodTime);
            assertThat(result.endPeriod()).isNull();
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodDoesNotConflict5() {
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
            var result = trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand);

            assertThat(result.trailerPlateNumber()).isEqualTo(testTrailer.getRegisterPlateNumber());
            assertThat(result.isCrossHitch()).isEqualTo(newCrossHitch);
            assertThat(result.startPeriod()).isNull();
            assertThat(result.endPeriod()).isEqualTo(newEndPeriodTime);
            assertThat(result.currentTruckBusinessId()).isEqualTo(newTruckBusinessId);
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTruckIdIsEmpty() {
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
                    trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("Truck business id is needed in this operation");
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodIsEmptyButNewTruckIdIsPresent() {
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
                    trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Wrong operation to unassign a truck");
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewStartDateIsAfterEndDate() {
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
                    trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("End period is before start period");

        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndTrailerNotExist() {
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
                    trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + trailerBusinessId);
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict() {
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
                    trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("The trailer is in use during the specified period.");
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict2() {
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
                    trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("The trailer is in use during the specified period.");
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict3() {
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
            var exception = catchException(() -> trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("The trailer is in use during the specified period.");
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict4() {
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
            var exception = catchException(() -> trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Processing trailer is currently assigned to a truck without end period. To add new assign edit first current assignment date or fill end date of new assign");
        }

        @Test
        void assignTrailerManageByBusinessIdWhenCommandIsValidAndNewTimePeriodHasConflict5() {
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
            var exception = catchException(() -> trailerApi.assignTrailerManageByBusinessId(trailerBusinessId, updateTrailerCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Processing trailer is currently assigned to a truck without start period. To add new assign edit first current assignment date or fill start date of new assign");
        }

        @Test
        void unassignTrailerManageByBusinessIdWhenCommandIsValidAndTruckIsAssigned() {
            //given
            var unassignCommand = new UnassignTrailerCommand(trailerBusinessId, true);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            TrailerDTO trailerDTO = trailerApi.unassignTrailerManageByBusinessId(trailerBusinessId, unassignCommand);

            assertThat(trailerDTO.startPeriod()).isNull();
            assertThat(trailerDTO.endPeriod()).isNull();
            assertThat(trailerDTO.currentTruckBusinessId()).isNull();

            assertThat(testTruck.getStartPeriodDate()).isNull();
            assertThat(testTruck.getEndPeriodDate()).isNull();
            assertThat(testTruck.getCurrentTrailerBusinessId()).isNull();
        }

        @Test
        void unassignTrailerManageByBusinessIdWhenCommandIsValidAndTruckIsUnassigned() {
            //given
            var unassignCommand = new UnassignTrailerCommand(trailerBusinessId, true);
            testTrailer.setCurrentTruckBusinessId(null);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));

            //then
            var exception = catchException(() ->
                    trailerApi.unassignTrailerManageByBusinessId(trailerBusinessId, unassignCommand));

            assertThat(exception).isExactlyInstanceOf(IllegalStateException.class);
            assertThat(exception.getMessage()).isEqualTo("Current trailer is already unassigned");
        }

        @Test
        void unassignTrailerManageByBusinessIdWhenCommandIsValidAndTrailerNotExist() {
            //given
            var unassignCommand = new UnassignTrailerCommand(trailerBusinessId, true);

            //when
            when(trailerRepository.findByBusinessId(trailerBusinessId)).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.unassignTrailerManageByBusinessId(trailerBusinessId, unassignCommand));

            assertThat(exception).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getMessage()).isEqualTo("No trailer with business id " + trailerBusinessId);
        }

        @Test
        void unassignTrailerManageByBusinessIdWhenCommandIsValidAndTrailerExistButAssignedTruckNotExist() {
            //given
            var unassignCommand = new UnassignTrailerCommand(trailerBusinessId, false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            TrailerDTO trailerDTO = trailerApi.unassignTrailerManageByBusinessId(trailerBusinessId, unassignCommand);

            assertThat(trailerDTO.startPeriod()).isNull();
            assertThat(trailerDTO.endPeriod()).isNull();
            assertThat(trailerDTO.currentTruckBusinessId()).isNull();
        }

        @Test
        void unassignTrailerManageByBusinessIdWhenCommandIsNotValid() {
            //given
            var unassignCommand = new UnassignTrailerCommand(trailerBusinessId, true);

            //when
            when(trailerRepository.findByBusinessId(trailerBusinessId)).thenReturn(Optional.of(testTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.empty());

            //then
            var exception = catchException(() ->
                    trailerApi.unassignTrailerManageByBusinessId(trailerBusinessId, unassignCommand));

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
                    Optional.of(newEndDate), Optional.of(crossHitchTruckBusinessId));

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTrailer))
                    .thenReturn(Optional.of(crossHitchTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTruck))
                    .thenReturn(Optional.of(crossHitchTruck));

            //then
            String result = trailerApi.crossHitchOperation(trailerBusinessId, updateAssignment);

            assertThat(testTrailer.getCurrentTruckBusinessId()).isEqualTo(crossHitchTruckBusinessId);
            assertThat(testTrailer.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(testTrailer.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTruck.getCurrentTrailerBusinessId()).isEqualTo(trailerBusinessId);
            assertThat(crossHitchTruck.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(crossHitchTruck.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTrailer.getCurrentTruckBusinessId()).isEqualTo(testTruck.getBusinessId());
            assertThat(crossHitchTrailer.getStartPeriodDate()).isEqualTo(startPeriodDate2);
            assertThat(crossHitchTrailer.getEndPeriodDate()).isEqualTo(endPeriodDate2);

            assertThat(testTruck.getCurrentTrailerBusinessId()).isEqualTo(crossHitchTrailer.getBusinessId());
            assertThat(testTruck.getStartPeriodDate()).isEqualTo(startPeriodDate2);
            assertThat(testTruck.getEndPeriodDate()).isEqualTo(endPeriodDate2);
            assertThat(result).isEqualTo("Cross hitch operation on processing trailer success. ");
        }

        @Test
        void crossHitchOperationWhenCommandIsValidAndTruckExistAndSecondTrailerIsCrossHitchNotAvailable() {
            //given
            var newStartDate = Instant.parse("2024-01-03T00:00:00Z");
            var newEndDate = Instant.parse("2024-01-04T00:00:00Z");
            var updateAssignment = new UpdateAssignmentTrailerCommand(
                    Optional.of(true), Optional.of(newStartDate),
                    Optional.of(newEndDate), Optional.of(crossHitchTruckBusinessId));
            crossHitchTrailer.setCrossHitch(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTrailer))
                    .thenReturn(Optional.of(crossHitchTrailer));
            when(truckRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTruck))
                    .thenReturn(Optional.of(crossHitchTruck));


            //then
            String result = trailerApi.crossHitchOperation(trailerBusinessId, updateAssignment);

            assertThat(testTrailer.getCurrentTruckBusinessId()).isEqualTo(crossHitchTruckBusinessId);
            assertThat(testTrailer.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(testTrailer.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTruck.getCurrentTrailerBusinessId()).isEqualTo(trailerBusinessId);
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
                    Optional.of(newEndDate), Optional.of(crossHitchTruckBusinessId));
            crossHitchTrailer.setCrossHitch(false);

            //when
            when(trailerRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTrailer))
                    .thenReturn(Optional.empty());
            when(truckRepository.findByBusinessId(any(UUID.class)))
                    .thenReturn(Optional.of(testTruck))
                    .thenReturn(Optional.of(crossHitchTruck));


            //then
            String result = trailerApi.crossHitchOperation(trailerBusinessId, updateAssignment);

            assertThat(testTrailer.getCurrentTruckBusinessId()).isEqualTo(crossHitchTruckBusinessId);
            assertThat(testTrailer.getStartPeriodDate()).isEqualTo(newStartDate);
            assertThat(testTrailer.getEndPeriodDate()).isEqualTo(newEndDate);

            assertThat(crossHitchTruck.getCurrentTrailerBusinessId()).isEqualTo(trailerBusinessId);
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
            doNothing().when(trailerRepository).deleteByBusinessId(any(UUID.class));

            //then
            trailerApi.deleteTrailerByBusinessId(trailerBusinessId);
            verify(trailerRepository, times(1)).deleteByBusinessId(trailerBusinessId);
        }
    }
}