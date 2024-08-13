package pl.jkuznik.trucktracking.domain.truck;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {TruckService.class, MethodValidationPostProcessor.class})
class TruckServiceTest {

    @Nested
    class AddMethodsTests {

        @Test
        void addTruck() {
        }

    }
    @Nested
    class GetMethodsTests {


        @Test
        void getTruckByBusinessId() {
        }

        @Test
        void getAllTrucks() {
        }

        @Test
        void getTruckUsedInLastMonthIfAssignPeriodTimeIsPresentAndEndPeriodTimeIsMatch() {

        }

    }

    @Nested
    class UpdateMethodsTests {

        @Test
        void updateTruckAssignByBusinessId() {
        }
    }

    @Nested
    class DeleteMethodsTests {

        @Test
        void deleteTruckByBusinessId() {
        }
    }
}