package pl.jkuznik.trucktracking.domain.trailer;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrailerTest {

    private final Trailer TRAILER = new Trailer();
    private final Instant CURRENT_START_DATE = Instant.now().plusSeconds(10);
    private final Instant CURRENT_END_DATE = Instant.now().plusSeconds(20);
    private final Instant BEFORE_START_DATE = Instant.now().plusSeconds(5);
    private final Instant BETWEEN_START_AND_END_DATE = Instant.now().plusSeconds(15);
    private final Instant AFTER_END_DATE = Instant.now().plusSeconds(25);

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