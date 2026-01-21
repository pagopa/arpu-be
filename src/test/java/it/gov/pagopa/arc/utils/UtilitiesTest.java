package it.gov.pagopa.arc.utils;

import it.gov.pagopa.arc.exception.custom.ZendeskAssistanceInvalidUserEmailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UtilitiesTest {

    @ParameterizedTest
    @CsvSource(value = {
            "someone@email.com, someone",
            "test.someone@email.it, test.someone",
            "some.one.!test#@email.com, some.one.!test#"
    })
    void givenCorrectEmailFormatWhenCallExtractNameFromEmailAssistanceTokenThenReturnName(String email, String name){
        //when
        String nameFromEmailAssistanceToken = Utilities.extractNameFromEmailAssistanceToken(email);

        //then
        assertEquals(nameFromEmailAssistanceToken, name);
    }

    @Test
    void givenEmptyEmailStringWhenExtractNameFromEmailAssistanceTokenThenReturnException() {
        //given
        String wrongEmail = "";
        //when
        //then
        ZendeskAssistanceInvalidUserEmailException  exception = assertThrows(ZendeskAssistanceInvalidUserEmailException .class,
                () -> Utilities.extractNameFromEmailAssistanceToken(wrongEmail));
        Assertions.assertEquals("Invalid user email []",exception.getMessage());

    }

    @Test
    void givenWrongEmailStringWhenExtractNameFromEmailAssistanceTokenThenReturnException() {
        //given
        String wrongEmail = "email";
        //when
        //then
        ZendeskAssistanceInvalidUserEmailException exception = assertThrows(ZendeskAssistanceInvalidUserEmailException .class,
                () -> Utilities.extractNameFromEmailAssistanceToken(wrongEmail));
        Assertions.assertEquals("Invalid user email [email]",exception.getMessage());

    }

    public static void setTraceId(String traceId) {
        MDC.put("traceId", traceId);
    }
    public static void clearTraceIdContext(){
        MDC.clear();
    }
}