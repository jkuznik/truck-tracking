package pl.jkuznik.trucktracking.domain.trailer;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void isInUseWhenAllValuesArePresentAndShouldReturnTrue() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE, BETWEEN_START_AND_END_DATE);

        //then
        assertTrue(inUse);
    }

    @Test
    void isInUseWhenAllValuesArePresentAndShouldReturnTrue2() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE, AFTER_END_DATE);

        //then
        assertTrue(inUse);
    }

    @Test
    void isInUseWhenAllValuesArePresentAndShouldReturnTrue3() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BETWEEN_START_AND_END_DATE, AFTER_END_DATE);

        //then
        assertTrue(inUse);
    }

    @Test
    void isInUseWhenAllValuesArePresentAndShouldReturnFalse() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE, BEFORE_START_DATE.plusSeconds(1));

        //then
        assertFalse(inUse);
    }

    @Test
    void isInUseWhenAllValuesArePresentAndShouldReturnFalse2() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(AFTER_END_DATE, AFTER_END_DATE.plusSeconds(1));

        //then
        assertFalse(inUse);
    }

    @Test
    void isInUseWhenCurrentEndDateIsEmptyAndNewValuesArePresentAndShouldReturnFalse() {
        //given
        TRAILER.setStartPeriodDate(CURRENT_START_DATE);
        TRAILER.setEndPeriodDate(null);

        //when
        boolean inUse = TRAILER.isInUse(BEFORE_START_DATE.minusSeconds(5), BEFORE_START_DATE);

        //then
        assertFalse(inUse);
    }

    @Test
    void isInUseWhenCurrentStartDateIsEmptyAndNewValuesArePresentAndShouldReturnFalse() {
        //given
        TRAILER.setStartPeriodDate(null);
        TRAILER.setEndPeriodDate(CURRENT_END_DATE);

        //when
        boolean inUse = TRAILER.isInUse(AFTER_END_DATE, AFTER_END_DATE.plusSeconds(5) );

        //then
        assertFalse(inUse);
    }
}