//package pl.jkuznik.trucktracking.domain.truck;
//
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
//import pl.jkuznik.trucktracking.domain.trailer.Trailer;
//import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
//import pl.jkuznik.trucktracking.domain.truck.api.TruckApi;
//import pl.jkuznik.trucktracking.domain.truck.api.dto.TruckDTO;
//import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TTHRepository;
//import pl.jkuznik.trucktracking.domain.truckTrailerHistory.TruckTrailerHistory;
//
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@SpringJUnitConfig(classes = {TruckService.class, MethodValidationPostProcessor.class})
//class TruckServiceTest {
//
//    @Autowired
//    TruckApi truckApi;
//
//    @MockBean
//    TruckRepository truckRepository;
//
//    @MockBean
//    TrailerRepository trailerRepository;
//
//    @MockBean
//    TTHRepository tthRepository;
//
//
//    private final String TRAILER_REGISTER_NUMBER = "TRAILER001";
//    private final String TRAILER_CROSS_HITCH_REGISTER_NUMBER = "TRAILER002";
//    private final String TRUCK_REGISTER_NUMBER = "TRUCK001";
//    private final String TRUCK_CROSS_HITCH_REGISTER_NUMBER = "TRUCK002";
//    private final UUID TRAILER_BUSINESS_ID = UUID.randomUUID();
//    private final UUID TRAILER_CROSS_HITCH_BUSINESS_ID = UUID.randomUUID();
//    private final UUID TRUCK_BUSINESS_ID = UUID.randomUUID();
//    private final UUID TRUCK_CROSS_HITCH_BUSINESS_ID = UUID.randomUUID();
//    private final Instant START_PERIOD_TIME = Instant.parse("2024-01-01T00:00:00Z");
//    private final Instant START_PERIOD_TIME2 = Instant.parse("2024-01-11T00:00:00Z");
//    private final Instant END_PERIOD_TIME = Instant.parse("2024-01-10T00:00:00Z");
//    private final Instant END_PERIOD_TIME2 = Instant.parse("2024-01-20T00:00:00Z");
//    private Trailer testTrailer = new Trailer(TRUCK_BUSINESS_ID, TRAILER_REGISTER_NUMBER);
//    private Truck testTruck = new Truck(TRUCK_BUSINESS_ID, TRUCK_REGISTER_NUMBER);
//    private Trailer crossHitchTrailer = new Trailer(TRAILER_CROSS_HITCH_BUSINESS_ID, TRAILER_CROSS_HITCH_REGISTER_NUMBER);
//    private Truck crossHitchTruck = new Truck(TRUCK_CROSS_HITCH_BUSINESS_ID, TRUCK_CROSS_HITCH_REGISTER_NUMBER);
//
//
//    @BeforeEach
//    void setUp() {
//        //given
//        testTrailer.setCrossHitch(true);
//        testTrailer.setStartPeriodDate(START_PERIOD_TIME);
//        testTrailer.setEndPeriodDate(END_PERIOD_TIME);
//        testTrailer.setCurrentTruckBusinessId(TRUCK_BUSINESS_ID);
//
//        testTruck.setStartPeriodDate(START_PERIOD_TIME);
//        testTruck.setEndPeriodDate(END_PERIOD_TIME);
//        testTruck.setCurrentTrailerBusinessId(TRUCK_BUSINESS_ID);
//
//        crossHitchTrailer.setCrossHitch(true);
//        crossHitchTrailer.setStartPeriodDate(START_PERIOD_TIME2);
//        crossHitchTrailer.setEndPeriodDate(END_PERIOD_TIME2);
//        crossHitchTrailer.setCurrentTruckBusinessId(TRUCK_CROSS_HITCH_BUSINESS_ID);
//
//        crossHitchTruck.setStartPeriodDate(START_PERIOD_TIME2);
//        crossHitchTruck.setEndPeriodDate(END_PERIOD_TIME2);
//        crossHitchTruck.setCurrentTrailerBusinessId(TRAILER_CROSS_HITCH_BUSINESS_ID);
//    }
//
//    @Nested
//    class AddMethodsTests {
//
//        @Test
//        void addTruck() {
//        }
//
//    }
//    @Nested
//    class GetMethodsTests {
//
//
//        @Test
//        void getTruckByBusinessId() {
//        }
//
//        @Test
//        void getAllTrucks() {
//        }
//
//        @Test
//        void getTruckUsedInLastMonthIfAssignPeriodTimeIsPresentAndEndPeriodTimeIsMatch() {
//            //given
//            //  startPeriodTime = "2024-01-01T00:00:00Z" , endPeriodTime = "2024-01-10T00:00:00Z"
//            var tth = new TruckTrailerHistory();
//            tth.setTruck(testTruck);
//            tth.setTrailer(testTrailer);
//            tth.setStartPeriodDate(START_PERIOD_TIME);
//            tth.setEndPeriodDate(END_PERIOD_TIME);
//
//            List<TruckTrailerHistory> tthList = List.of(tth);
//            PageImpl<TruckTrailerHistory> tthPage = new PageImpl<>(tthList);
//
//            var truckDTO = new TruckDTO(testTruck.getRegisterPlateNumber(), testTruck.getBusinessId(),
//                    testTruck.getStartPeriodDate(), testTruck.getEndPeriodDate(), testTrailer.getBusinessId());
//
//            Instant mockNow = Instant.parse("2024-02-05T00:00:00Z");
//
//            ZoneId zoneId = ZoneId.systemDefault();
//            ZonedDateTime zdt = ZonedDateTime.ofInstant(mockNow, zoneId);
//            Instant monthAgo = zdt.minusMonths(1).toInstant();
//
//            //when
//
//            //then
//            Page<TruckDTO> result = truckApi.getAllTrucksUsedInLastMonth(1, 25);
//
//            assertThat(result).contains(truckDTO);
//
//        }
//
//    }
//
//    @Nested
//    class UpdateMethodsTests {
//
//        @Test
//        void updateTruckAssignByBusinessId() {
//        }
//    }
//
//    @Nested
//    class DeleteMethodsTests {
//
//        @Test
//        void deleteTruckByBusinessId() {
//        }
//    }
//}