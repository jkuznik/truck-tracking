package pl.jkuznik.trucktracking.domain.truckTrailerHistory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.jkuznik.trucktracking.domain.bootstrap.Bootstrap;
import pl.jkuznik.trucktracking.domain.trailer.Trailer;
import pl.jkuznik.trucktracking.domain.trailer.TrailerRepository;
import pl.jkuznik.trucktracking.domain.truck.Truck;
import pl.jkuznik.trucktracking.domain.truck.TruckRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
class TTHRepositoryImplTest {

    private final String TRAILER_REGISTER_NUMBER = "TRAILER001";
    private final String TRUCK_REGISTER_NUMBER = "TRUCK001";
    private final String TRAILER_CROSS_HITCH_REGISTER_NUMBER = "TRAILER002";
    private final String TRUCK_CROSS_HITCH_REGISTER_NUMBER = "TRUCK002";
    private final UUID TRAILER_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRUCK_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRAILER_CROSS_HITCH_BUSINESS_ID = UUID.randomUUID();
    private final UUID TRUCK_CROSS_HITCH_BUSINESS_ID = UUID.randomUUID();

    private final ZoneId zoneId = ZoneId.systemDefault();
    private final Instant START_PERIOD_TIME = Instant.from(ZonedDateTime.ofInstant(Instant.now(), zoneId).minusMonths(1).plusDays(5));
    private final Instant END_PERIOD_TIME = Instant.from(ZonedDateTime.ofInstant(Instant.now(), zoneId).minusMonths(1).plusDays(15));
    private final Instant START_PERIOD_TIME2 = Instant.from(ZonedDateTime.ofInstant(Instant.now(), zoneId).minusMonths(1).plusDays(16));
    private final Instant END_PERIOD_TIME2 = Instant.from(ZonedDateTime.ofInstant(Instant.now(), zoneId).minusMonths(1).plusDays(26));
    private final Instant MONTH_AGO = Instant.from(ZonedDateTime.ofInstant(Instant.now(), zoneId).minusMonths(1));
    private final Instant DATE_BEFORE_MONTH_AGO = Instant.from(ZonedDateTime.ofInstant(Instant.now(), zoneId).minusMonths(1).minusDays(5));

    private Trailer testTrailer = new Trailer(TRAILER_BUSINESS_ID, TRAILER_REGISTER_NUMBER);
    private Truck testTruck = new Truck(TRUCK_BUSINESS_ID, TRUCK_REGISTER_NUMBER);
    private Trailer crossHitchTrailer = new Trailer(TRAILER_CROSS_HITCH_BUSINESS_ID, TRAILER_CROSS_HITCH_REGISTER_NUMBER);
    private Truck crossHitchTruck = new Truck(TRUCK_CROSS_HITCH_BUSINESS_ID, TRUCK_CROSS_HITCH_REGISTER_NUMBER);

    private final PageRequest pageRequest = PageRequest.of(0, 100);

    @Autowired
    TrailerRepository trailerRepository;

    @Autowired
    TruckRepository truckRepository;

    @Autowired
    TTHRepositoryImpl tthRepository;

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

        crossHitchTrailer.setCrossHitch(true);
        crossHitchTrailer.setStartPeriodDate(START_PERIOD_TIME2);
        crossHitchTrailer.setEndPeriodDate(END_PERIOD_TIME2);
        crossHitchTrailer.setCurrentTruckBusinessId(TRUCK_CROSS_HITCH_BUSINESS_ID);

        crossHitchTruck.setStartPeriodDate(START_PERIOD_TIME2);
        crossHitchTruck.setEndPeriodDate(END_PERIOD_TIME2);
        crossHitchTruck.setCurrentTrailerBusinessId(TRAILER_CROSS_HITCH_BUSINESS_ID);

        TruckTrailerHistory truckTrailerHistory2 = new TruckTrailerHistory();
        truckTrailerHistory2.setTrailer(crossHitchTrailer);
        truckTrailerHistory2.setTruck(crossHitchTruck);
        truckTrailerHistory2.setStartPeriodDate(crossHitchTruck.getStartPeriodDate());
        truckTrailerHistory2.setEndPeriodDate(crossHitchTruck.getEndPeriodDate());

        trailerRepository.save(testTrailer);
        truckRepository.save(testTruck);
        trailerRepository.save(crossHitchTrailer);
        truckRepository.save(crossHitchTruck);

        tthRepository.save(truckTrailerHistory);
        tthRepository.save(truckTrailerHistory2);
        Bootstrap bootstrap = new Bootstrap(trailerRepository, truckRepository, tthRepository);
    }

//    @Test
//    void checkBootstrap() {
//        //given
//        long count = trailerRepository.count();
//
//        assertTrue(count > 50);
//    }

    @Test
    void getTruckUsedInLastMonthDemonstration() {
        Page<Truck> truckUsedInLastMonth = tthRepository.getTruckUsedInLastMonth(pageRequest);
        List<Truck> trucks = truckUsedInLastMonth.getContent();

        System.out.println("Wyniki\n");
        trucks.forEach(truck -> System.out.println(truck.getRegisterPlateNumber() + " " + truck.getStartPeriodDate() +
                " " + truck.getEndPeriodDate()));
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBeforeMonthAgoAndEndDateIsBeforeMonthAgo() {
        //given
        testTrailer.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTrailer.setEndPeriodDate(DATE_BEFORE_MONTH_AGO.plusSeconds(5));
        testTruck.setEndPeriodDate(DATE_BEFORE_MONTH_AGO.plusSeconds(5));
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        TruckTrailerHistory testedTTH = tthRepository.findAll().getLast();

        assertThat(result.getRegisterPlateNumber()).isNotEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isNotEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isNotEqualTo(testTruck.getEndPeriodDate());

        assertThat(testedTTH.getTruck().getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(testedTTH.getStartPeriodDate()).isBefore(MONTH_AGO);
        assertThat(testedTTH.getEndPeriodDate()).isBefore(MONTH_AGO);

        assertThat(result).isNotEqualTo(testedTTH.getTruck());
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBeforeMonthAgoAndEndDateIsBetweenMonthAgoAndNow() {
        //given
        testTrailer.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isEqualTo(testTruck.getEndPeriodDate());

        assertThat(result.getStartPeriodDate()).isBefore(MONTH_AGO);
        assertThat(result.getEndPeriodDate()).isAfter(MONTH_AGO);
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBetweenMonthAgoAndNowAndEndDateIsBetweenMonthAgoAndNow() {
        //given
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isEqualTo(testTruck.getEndPeriodDate());

        assertThat(result.getStartPeriodDate()).isAfter(MONTH_AGO);
        assertThat(result.getEndPeriodDate()).isAfter(MONTH_AGO);
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBetweenMonthAgoAndNowAndEndDateIsAfterNow() {
        //given
        var futureDate = Instant.now().plusSeconds(3600*24*10);
        testTrailer.setEndPeriodDate(futureDate);
        testTruck.setEndPeriodDate(futureDate);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isEqualTo(testTruck.getEndPeriodDate());

        assertThat(result.getStartPeriodDate()).isAfter(MONTH_AGO);
        assertThat(result.getStartPeriodDate()).isBefore(Instant.now());
        assertThat(result.getEndPeriodDate()).isAfter(Instant.now());
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsAfterNowAndEndDateIsAfterNow() {
        //given
        var futureDate = Instant.now().plusSeconds(3600*24*10);
        var futureDate2 = Instant.now().plusSeconds(3600*24*20);
        testTrailer.setStartPeriodDate(futureDate);
        testTruck.setStartPeriodDate(futureDate);
        testTrailer.setEndPeriodDate(futureDate2);
        testTruck.setEndPeriodDate(futureDate2);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        TruckTrailerHistory testedTTH = tthRepository.findAll().getLast();

        assertThat(result.getRegisterPlateNumber()).isNotEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isNotEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isNotEqualTo(testTruck.getEndPeriodDate());

        assertThat(testedTTH.getTruck().getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(testedTTH.getStartPeriodDate()).isAfter(Instant.now());
        assertThat(testedTTH.getEndPeriodDate()).isAfter(Instant.now());

        assertThat(result).isNotEqualTo(testedTTH.getTruck());
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBeforeMonthAgoAndEndDateIsNull() {
        //given
        testTrailer.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTrailer.setEndPeriodDate(null);
        testTruck.setEndPeriodDate(null);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isNull();

        assertThat(result.getStartPeriodDate()).isBefore(MONTH_AGO);
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBetweenMonthAgoAndNowAndEndDateIsNull() {
        //given
        testTrailer.setEndPeriodDate(null);
        testTruck.setEndPeriodDate(null);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isNull();

        assertThat(result.getStartPeriodDate()).isAfter(MONTH_AGO);
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsAfterNowAndEndDateIsNull() {
        //given
        testTrailer.setStartPeriodDate(Instant.now().plusSeconds(3600*24*10));
        testTruck.setStartPeriodDate(Instant.now().plusSeconds(3600*24*10));
        testTrailer.setEndPeriodDate(null);
        testTruck.setEndPeriodDate(null);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        var testedTTH = tthRepository.findAll().getLast();

        assertThat(result.getRegisterPlateNumber()).isNotEqualTo("TEST REGISTER PLATE NUMBER");

        assertThat(testedTTH.getTruck().getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(testedTTH.getStartPeriodDate()).isAfter(Instant.now());
        assertThat(testedTTH.getEndPeriodDate()).isNull();
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsNullAndEndDateIsBeforeMonthAgo() {
        //given
        testTrailer.setStartPeriodDate(null);
        testTruck.setStartPeriodDate(null);
        testTrailer.setEndPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setEndPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        TruckTrailerHistory testedTTH = tthRepository.findAll().getLast();

        assertThat(result.getRegisterPlateNumber()).isNotEqualTo("TEST REGISTER PLATE NUMBER");

        assertThat(testedTTH.getTruck().getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(testedTTH.getStartPeriodDate()).isNull();
        assertThat(testedTTH.getEndPeriodDate()).isBefore(MONTH_AGO);
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsNullAndEndDateIsBetweenMonthAgoAndNow() {
        //given
        testTrailer.setStartPeriodDate(null);
        testTruck.setStartPeriodDate(null);
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isNull();
        assertThat(result.getEndPeriodDate()).isEqualTo(testTruck.getEndPeriodDate());

        assertThat(result.getEndPeriodDate()).isAfter(MONTH_AGO);
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsNullAndEndDateIsAfterNow() {
        //given
        testTrailer.setStartPeriodDate(null);
        testTruck.setStartPeriodDate(null);
        testTrailer.setEndPeriodDate(Instant.now().plusSeconds(3600*24*10));
        testTruck.setEndPeriodDate(Instant.now().plusSeconds(3600*24*10));
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isNull();
        assertThat(result.getEndPeriodDate()).isEqualTo(testTruck.getEndPeriodDate());

        assertThat(result.getEndPeriodDate()).isAfter(Instant.now());
    }

    @Test
    void getTruckUsedInLastMonthWhenStartDateIsBeforeMonthAgoAndEndDateIsAfterNow() {
        //given
        testTrailer.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTruck.setStartPeriodDate(DATE_BEFORE_MONTH_AGO);
        testTrailer.setEndPeriodDate(Instant.now().plusSeconds(3600*24*10));
        testTruck.setEndPeriodDate(Instant.now().plusSeconds(3600*24*10));
        testTruck.setRegisterPlateNumber("TEST REGISTER PLATE NUMBER");

        var tth = new TruckTrailerHistory();
        tth.setTruck(testTruck);
        tth.setTrailer(testTrailer);
        tth.setStartPeriodDate(testTruck.getStartPeriodDate());
        tth.setEndPeriodDate(testTruck.getEndPeriodDate());

        tthRepository.save(tth);

        //then
        Page<Truck> truckUsed = tthRepository.getTruckUsedInLastMonth(pageRequest);
        Truck result = truckUsed.getContent().getLast();

        assertThat(result.getRegisterPlateNumber()).isEqualTo("TEST REGISTER PLATE NUMBER");
        assertThat(result.getStartPeriodDate()).isEqualTo(testTruck.getStartPeriodDate());
        assertThat(result.getEndPeriodDate()).isEqualTo(testTruck.getEndPeriodDate());

        assertThat(result.getStartPeriodDate()).isBefore(MONTH_AGO);
        assertThat(result.getEndPeriodDate()).isAfter(Instant.now());
    }


}