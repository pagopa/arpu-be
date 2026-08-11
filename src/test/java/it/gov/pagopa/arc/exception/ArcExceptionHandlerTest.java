package it.gov.pagopa.arc.exception;

import it.gov.pagopa.arc.exception.common.CommonExceptionHandlerTest;
import it.gov.pagopa.arc.exception.custom.InvalidTokenException;
import it.gov.pagopa.arc.exception.custom.ZendeskAssistanceInvalidUserEmailException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;

class ArcExceptionHandlerTest extends CommonExceptionHandlerTest {

    @Test
    void givenInvalidTokenThenHandleInvalidTokenException() throws Exception {
        doThrow(new InvalidTokenException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("UNAUTHORIZED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("INVALID_TOKEN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

    @Test
    void givenInvalidEmailWhenExtractNameFromEmailAssistanceTokenThenHandleZendeskAssistanceInvalidUserEmailException() throws Exception {
        doThrow(new ZendeskAssistanceInvalidUserEmailException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("INVALID_ZENDESK_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
    }

}