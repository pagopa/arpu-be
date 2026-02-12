package it.gov.pagopa.arc.dto.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperUtilitiesTest {

    @ParameterizedTest
    @CsvSource(value = {
            "2024-04-11T06:56:14.845126, 2024-04-11T06:56:14Z",
            "2023-02-08T06:56:18.542235, 2023-02-08T06:56:18Z",
            "2023-02-08T06:56:18, 2023-02-08T06:56:18Z"
    })
    void givenDateTimeWithMillisWhenCallTruncateToSecondsThenReturnDateTimeWithoutMillis(LocalDateTime dateTime, ZonedDateTime expectedDateTime) {
        //when
        ZonedDateTime zonedDateTime = MapperUtilities.convertToZonedDateTimeAndTruncateSeconds(dateTime);
        //then
        Assertions.assertEquals(expectedDateTime, zonedDateTime);
        System.out.println(zonedDateTime);
    }

    @MethodSource("valueSource")
    void givenCalculateTotalAmountWhenThen(Long amount, Long fee, Long expectedTotalAmount) {
        //when
        Long totalAmountResult = MapperUtilities.calculateTotalAmount(amount, fee);
        //then
        assertEquals(expectedTotalAmount, totalAmountResult);
    }

    static Stream<Arguments> valueSource() {
        return Stream.of(
                Arguments.of(500L, 20L, 520L),
                Arguments.of(21L, 11L, 32L),
                Arguments.of(320L, null, 320L),
                Arguments.of(null, 19L, null),
                Arguments.of(null, null, null)
        );
    }
}