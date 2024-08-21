package pl.jkuznik.trucktracking.domain.truck;

import jakarta.persistence.EntityManagerFactory;
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
import pl.jkuznik.trucktracking.domain.bootstrap.Bootstrap;
import pl.jkuznik.trucktracking.domain.shared.TestEntityManagerFactoryImpl;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.trailer.api.command.AddTrailerCommand;
import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
import pl.jkuznik.trucktracking.domain.truck.api.command.AddTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.command.UpdateTruckCommand;
import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepositoryImpl;
import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(classes = {TruckService.class, TTHRepositoryImpl.class, TestEntityManagerFactoryImpl.class, MethodValidationPostProcessor.class})
class TruckServiceTest {

    @Autowired
    TruckApi truckApi;

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @MockBean
    TruckRepository truckRepository;

    @MockBean
    TrailerRepository trailerRepository;

    @MockBean
    TTHRepositoryImpl tthRepository;

    private final String TRAILER_REGISTER_NUMBER = "TRAILER001";
    private final String SECOND_TRAILER_REGISTER_NUMBER = "TRAILER002";
    private final String TRUCK_REGISTER_NUMBER = "TRUCK001";
    private final String SECOND_TRUCK_REGISTER_NUMBER = "TRUCK002";
    private final UUID TRAILER_BUSINESS_ID = UUID.randomUUID();
    private final UUID SECOND_TRAILER_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRUCK_BUSINESS_ID = UUID.randomUUID();
    private final UUID SECOND_TRUCK_BUSINESS_ID = UUID.randomUUID();
    private final Instant START_PERIOD_TIME = Instant.parse("2024-01-01T00:00:00Z");
    private final Instant START_PERIOD_TIME2 = Instant.parse("2024-01-11T00:00:00Z");
    private final Instant END_PERIOD_TIME = Instant.parse("2024-01-10T00:00:00Z");
    private final Instant END_PERIOD_TIME2 = Instant.parse("2024-01-20T00:00:00Z");
    private Trailer testTrailer = new Trailer(TRUCK_BUSINESS_ID, TRAILER_REGISTER_NUMBER);
    private Truck testTruck = new Truck(TRUCK_BUSINESS_ID, TRUCK_REGISTER_NUMBER);
    private Trailer secondTestTrailer = new Trailer(SECOND_TRAILER_BUSINESS_ID, SECOND_TRAILER_REGISTER_NUMBER);
    private Truck secondTestTruck = new Truck(SECOND_TRUCK_BUSINESS_ID, SECOND_TRUCK_REGISTER_NUMBER);


    @BeforeEach
    void setUp() {
        //given
        testTrailer.setCrossHitch(true);
        testTrailer.setStartPeriodDate(START_PERIOD_TIME);
        testTrailer.setEndPeriodDate(END_PERIOD_TIME);
        testTrailer.setCurrentTruckBusinessId(TRUCK_BUSINESS_ID);

        testTruck.setStartPeriodDate(START_PERIOD_TIME);
        testTruck.setEndPeriodDate(END_PERIOD_TIME);
        testTruck.setCurrentTrailerBusinessId(TRAILER_BUSINESS_ID);

        TruckTrailerHistory truckTrailerHistory = new TruckTrailerHistory();
        truckTrailerHistory.setTrailer(testTrailer);
        truckTrailerHistory.setTruck(testTruck);
        truckTrailerHistory.setStartPeriodDate(testTruck.getStartPeriodDate());
        truckTrailerHistory.setEndPeriodDate(testTruck.getEndPeriodDate());

        secondTestTrailer.setCrossHitch(true);
        secondTestTrailer.setStartPeriodDate(START_PERIOD_TIME2);
        secondTestTrailer.setEndPeriodDate(END_PERIOD_TIME2);
        secondTestTrailer.setCurrentTruckBusinessId(SECOND_TRUCK_BUSINESS_ID);

        secondTestTruck.setStartPeriodDate(START_PERIOD_TIME2);
        secondTestTruck.setEndPeriodDate(END_PERIOD_TIME2);
        secondTestTruck.setCurrentTrailerBusinessId(SECOND_TRAILER_BUSINESS_ID);

        TruckTrailerHistory truckTrailerHistory2 = new TruckTrailerHistory();
        truckTrailerHistory2.setTrailer(secondTestTrailer);
        truckTrailerHistory2.setTruck(secondTestTruck);
        truckTrailerHistory2.setStartPeriodDate(secondTestTruck.getStartPeriodDate());
        truckTrailerHistory2.setEndPeriodDate(secondTestTruck.getEndPeriodDate());

//        trailerRepository.save(testTrailer);
//        truckRepository.save(testTruck);
//        trailerRepository.save(secondTestTrailer);
//        truckRepository.save(secondTestTruck);
//
//        tthRepository.save(truckTrailerHistory);
//        tthRepository.save(truckTrailerHistory2);

//        Bootstrap bootstrap = new Bootstrap(trailerRepository, truckRepository, tthRepository);
    }

    @Nested
    class AddMethodsTests {

        @Test
        void addTruckWhenCommandIsValidAndTruckNotExist() {
            //given
            AddTruckCommand addTruckCommand = new AddTruckCommand(TRUCK_REGISTER_NUMBER);

            //when
            when(truckRepository.findByRegisterPlateNumber(TRUCK_REGISTER_NUMBER)).thenReturn(Optional.empty());
            when(truckRepository.save(any(Truck.class))).thenReturn(testTruck);

            //then
            TruckDTO newTruckDTO = truckApi.addTruck(addTruckCommand);

            verify(truckRepository, times(1)).save(any(Truck.class));
            assertThat(TRUCK_REGISTER_NUMBER).isEqualTo(newTruckDTO.truckPlateNumber());
        }

        @Test
        void addTruckWhenCommandIsValidAndTruckExist() {
            //given
            AddTruckCommand addTruckCommand = new AddTruckCommand(TRUCK_REGISTER_NUMBER);

            //when
            when(truckRepository.findByRegisterPlateNumber(TRUCK_REGISTER_NUMBER)).thenReturn(Optional.of(testTruck));

            //then
            var exception = catchException(() -> truckApi.addTruck(addTruckCommand));

            //TODO dodać swoje wyjątki
            assertThat(exception).isExactlyInstanceOf(RuntimeException.class);
            assertThat(exception.getMessage()).isEqualTo("Truck with " + addTruckCommand.registerPlateNumber() + " plate number already exists");
        }

        @Test
        void addTruckWhenCommandIsBlank() {
            //given
            AddTruckCommand addTruckCommand = new AddTruckCommand("");

            //when
            var exception = catchException(() -> truckApi.addTruck(addTruckCommand));

            //then
            assertThat(exception).isExactlyInstanceOf(ConstraintViolationException.class);
        }

        @Test
        void addTruckWhenCommandIsNull() {
            //when
            var exception = catchException(() -> truckApi.addTruck(null));

            //then
            assertThat(exception).isExactlyInstanceOf(ConstraintViolationException.class);
        }

    }
    @Nested
    class GetMethodsTests {


        @Test
        void getTruckByBusinessIdWhenTruckExist() {
            //when
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));

            //then
            TruckDTO result = truckApi.getTruckByBusinessId(TRAILER_BUSINESS_ID);

            assertThat(result.truckPlateNumber()).isEqualTo(testTruck.getRegisterPlateNumber());
        }

        @Test
        void getAllTrucks() {
            PageImpl<Truck> truckPage = new PageImpl<>(List.of(testTruck));

            //when
            when(truckRepository.findAll(PageRequest.of(0,25))).thenReturn(truckPage);

            //then
            Page<TruckDTO> trucks = truckApi.getAllTrucks(1, 25);

            assertThat(trucks.getContent().size()).isEqualTo(1);
            assertThat(trucks.getContent().getFirst().truckPlateNumber()).isEqualTo(testTruck.getRegisterPlateNumber());
        }

        @Test
        void getTruckUsedInLastMonth() {
            //given
            List<Truck> trucks = List.of(testTruck, secondTestTruck);
            Page<Truck> truckPage = new PageImpl<>(trucks);

            var truckDTO = new TruckDTO(testTruck.getRegisterPlateNumber(), testTruck.getBusinessId(),
                    testTruck.getStartPeriodDate(), testTruck.getEndPeriodDate(), testTruck.getCurrentTrailerBusinessId());

            //when
            when(tthRepository.getTruckUsedInLastMonth(PageRequest.of(0,25))).thenReturn(truckPage);

            //then
            Page<TruckDTO> result = truckApi.getAllTrucksUsedInLastMonth(1, 25);

            assertThat(result).contains(truckDTO);
        }

    }

    @Nested
    class UpdateMethodsTests {

        @Test
        void updateTruckAssignByBusinessId() {
            //given
            Instant newStartPeriodTime = Instant.parse("2024-01-03T00:00:00Z");
            Instant newEndPeriodTime = Instant.parse("2024-01-04T00:00:00Z");
            var updateTruckCommand = new UpdateTruckCommand(Optional.of(newStartPeriodTime), Optional.of(newEndPeriodTime),
                    Optional.of(secondTestTrailer.getBusinessId()));

            //when
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));
            when(trailerRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(secondTestTrailer));

            //then
            TruckDTO result = truckApi.updateTruckAssignByBusinessId(TRUCK_BUSINESS_ID, updateTruckCommand);

            assertThat(result.truckPlateNumber()).isEqualTo(testTruck.getRegisterPlateNumber());
            assertThat(result.startPeriod()).isEqualTo(newStartPeriodTime);
            assertThat(result.endPeriod()).isEqualTo(newEndPeriodTime);
            assertThat(result.currentTrailerBusinessId()).isEqualTo(secondTestTrailer.getBusinessId());
        }
    }

    @Nested
    class DeleteMethodsTests {
        @Test
        void deleteTruckByBusinessId() {

            //when
            when(truckRepository.findByBusinessId(any(UUID.class))).thenReturn(Optional.of(testTruck));
            doNothing().when(truckRepository).deleteByBusinessId(any(UUID.class));

            //then
            truckApi.deleteTruckByBusinessId(TRUCK_BUSINESS_ID);
            verify(truckRepository, times(1)).deleteByBusinessId(TRUCK_BUSINESS_ID);
        }
    }
}