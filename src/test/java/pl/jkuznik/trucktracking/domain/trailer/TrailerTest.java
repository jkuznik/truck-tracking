package pl.jkuznik.trucktracking.domain.trailer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TrailerTest {

    private final Trailer TRAILER = new Trailer();
    private final Instant CURRENT_START_DATE = Instant.parse("2020-06-10T00:00:00Z");
    private final Instant CURRENT_END_DATE = Instant.parse("2020-06-20T00:00:00Z");
    private final Instant BEFORE_START_DATE = Instant.parse("2020-06-09T00:00:00Z");
    private final Instant AFTER_START_DATE = Instant.parse("2020-06-11T00:00:00Z");
    private final Instant BETWEEN_START_AND_END_DATE = Instant.parse("2020-06-15T00:00:00Z");
    private final Instant BEFORE_END_DATE = Instant.parse("2020-06-19T00:00:00Z");
    private final Instant AFTER_END_DATE = Instant.parse("2020-06-21T00:00:00Z");

    @Test
    void isInUseAllValuesArePresentAndShouldReturnTrue() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE, BETWEEN_START_AND_END_DATE);

        //then
        assertTrue(inUse);
    }

    @Test
    void isInUseAllValuesArePresentAndShouldReturnTrue2() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE, AFTER_END_DATE);

        //then
        assertTrue(inUse);
    }

    @Test
    void isInUseAllValuesArePresentAndShouldReturnTrue3() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BETWEEN_START_AND_END_DATE, AFTER_END_DATE);

        //then
        assertTrue(inUse);
    }

    @Test
    void isInUseAllValuesArePresentAndShouldReturnFalse() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE, BEFORE_START_DATE.plusSeconds(1));

        //then
        assertFalse(inUse);
    }

    @Test
    void isInUseAllValuesArePresentAndShouldReturnFalse2() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(AFTER_END_DATE, AFTER_END_DATE.plusSeconds(1));

        //then
        assertFalse(inUse);
    }
}