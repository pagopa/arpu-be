package it.gov.pagopa.arc.exception;

import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.arc.exception.custom.*;
import it.gov.pagopa.arc.utils.MemoryAppender;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDate;

import static org.mockito.Mockito.doThrow;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(value = {ArcExceptionHandlerTest.TestController.class},
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        })
@ContextConfiguration(classes = {
    ArcExceptionHandlerTest.TestController.class,
    ArcExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class ArcExceptionHandlerTest {

    public static final String DATA = "data";
    public static final String HEADER = "header";

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private TestController testControllerSpy;

    private MemoryAppender memoryAppender;

    @RestController
    static class TestController {

        @GetMapping("/test")
        void testEndpoint() {
            //Needed for testing notice API
        }

    }

    @BeforeEach
    void setUp() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("it.gov.pagopa.arc.exception.ArcExceptionHandler");
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    void givenRequestWhenBizEventsServiceReturnErrorThenHandleBizEventsInvocationException() throws Exception {
        doThrow(new BizEventsInvocationException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("generic_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.BizEventsInvocationException occurred handling request GET /test: HttpStatus 500 - Error"));
    }

    @Test
    void givenRequestWhenBizEventsServiceReturnInvalidAmountThenHandleBizEventsInvalidAmountException() throws Exception {
        doThrow(new BizEventsInvalidAmountException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_amount"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.BizEventsInvalidAmountException occurred handling request GET /test: HttpStatus 400 - Error"));
    }

    @Test
    void givenRequestWhenBizEventsServiceReturnInvalidDateThenHandleBizEventsInvalidDateException() throws Exception {
        doThrow(new BizEventsInvalidDateException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_date"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.BizEventsInvalidDateException occurred handling request GET /test: HttpStatus 400 - Error"));
    }

    @Test
    void givenRequestWhenBizEventsServiceReturnTooManyRequestThenHandleBizEventsTooManyRequest() throws Exception {
        doThrow(new BizEventsTooManyRequestException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("too_many_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.BizEventsTooManyRequestException occurred handling request GET /test: HttpStatus 429 - Error"));
    }

    @Test
    void givenRequestWhenPullPaymentReturnBadRequestErrorThenHandlePullPaymentInvalidRequestException() throws Exception {
        doThrow(new PullPaymentInvalidRequestException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.PullPaymentInvalidRequestException occurred handling request GET /test: HttpStatus 400 - Error"));
    }

    @Test
    void givenRequestWhenPullPaymentReturnErrorThenHandlePullPaymentInvocationException() throws Exception {
        doThrow(new PullPaymentInvocationException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("generic_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.PullPaymentInvocationException occurred handling request GET /test: HttpStatus 500 - Error"));
    }

    @Test
    void givenRequestWhenPullPaymentServiceReturnTooManyRequestThenHandlePullPaymentTooManyRequestException() throws Exception {
        doThrow(new PullPaymentTooManyRequestException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("too_many_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.PullPaymentTooManyRequestException occurred handling request GET /test: HttpStatus 429 - Error"));
    }

     @Test
    void givenInvalidTokenThenHandleInvalidTokenException() throws Exception {
        doThrow(new InvalidTokenException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("auth_user_unauthorized"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.InvalidTokenException occurred handling request GET /test: HttpStatus 401 - Error"));
    }

    @Test
    void givenInvalidEmailWhenExtractNameFromEmailAssistanceTokenThenHandleZendeskAssistanceInvalidUserEmailException() throws Exception {
        doThrow(new ZendeskAssistanceInvalidUserEmailException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_email"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.ZendeskAssistanceInvalidUserEmailException occurred handling request GET /test: HttpStatus 400 - Error"));
    }

    @Test
    void givenRequestWhenBizEventsServiceReturnNotFoundErrorThenHandleBizEventsPaidNoticeNotFoundExceptionError() throws Exception {
        doThrow(new BizEventsPaidNoticeNotFoundException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("notice_not_found_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.BizEventsPaidNoticeNotFoundException occurred handling request GET /test: HttpStatus 404 - Error"));
    }

    @Test
    void givenGenericErrorThenThrowException() throws Exception {
        doThrow(new RuntimeException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is(500))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("generic_error"));
    }

    @Test
    void givenRequestWhenBizEventsServiceReturnNotFoundErrorThenHandleBizEventsNotFoundExceptionPdfError() throws Exception {
        doThrow(new BizEventsReceiptNotFoundException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("receipt_not_found_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.BizEventsReceiptNotFoundException occurred handling request GET /test: HttpStatus 404 - Error"));
    }

    @Test
    void givenRequestWhenGPDReturnBadRequestErrorThenHandleGPDInvalidRequestException() throws Exception {
        doThrow(new GPDInvalidRequestException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.GPDInvalidRequestException occurred handling request GET /test: HttpStatus 400 - Error"));
    }

    @Test
    void givenRequestWhenGPDReturnNotFoundErrorThenHandleGPDPaymentNoticeDetailsNotFoundException() throws Exception {
        doThrow(new GPDPaymentNoticeDetailsNotFoundException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("payment_notice_not_found_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.GPDPaymentNoticeDetailsNotFoundException occurred handling request GET /test: HttpStatus 404 - Error"));
    }

    @Test
    void givenRequestWhenGPDReturnErrorThenHandleGPDInvocationException() throws Exception {
        doThrow(new GPDInvocationException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("generic_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.GPDInvocationException occurred handling request GET /test: HttpStatus 500 - Error"));
    }

    @Test
    void givenRequestWhenGPDServiceReturnTooManyRequestThenHandleGPDTooManyRequestException() throws Exception {
        doThrow(new GPDTooManyRequestException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("too_many_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class it.gov.pagopa.arc.exception.custom.GPDTooManyRequestException occurred handling request GET /test: HttpStatus 429 - Error"));
    }

    @Test
    void givenWrongParameterWhenRequestThenHandleConstraintViolationException() throws Exception {
        doThrow(new ConstraintViolationException("error",null)).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A ConstraintViolationException occurred handling request GET: HttpStatus 400 - /test"));
    }

    @Test
    void givenWrongFormatWhenRequestThenHandleMethodArgumentTypeMismatchException() throws Exception {

        doThrow(new MethodArgumentTypeMismatchException("error", LocalDate.class, "dueDate", null, null )).when(testControllerSpy).testEndpoint();
        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("invalid_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Method parameter 'dueDate': Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate'"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A MethodArgumentTypeMismatchException occurred handling request GET: HttpStatus 400 - /test"));
    }

    @Test
    void givenHttpClientErrorExceptionWithValidHttpStatusWhenRequestThenHandleHttpClientErrorException() throws Exception {
        doThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("generic_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("403 Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class org.springframework.web.client.HttpClientErrorException occurred handling request GET /test: HttpStatus 403 - 403 Error"));
    }

    @Test
    void givenHttpClientErrorExceptionWithInvalidHttpStatusWhenRequestThenHandleHttpClientErrorException() throws Exception {
        HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(999);
        doThrow(new HttpClientErrorException(httpStatusCode, "Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("generic_error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value(httpStatusCode+" Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class org.springframework.web.client.HttpClientErrorException occurred handling request GET /test: HttpStatus 500 - 999 Error"));
    }

    @Test
    void givenValidationErrorWhenRequestThenHandleValidationException() throws Exception {
        doThrow(new ValidationException("Error")).when(testControllerSpy).testEndpoint();

        mockMvc.perform(MockMvcRequestBuilders.get("/test")
                        .param(DATA, DATA)
                        .header(HEADER,HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("bad_request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error_description").value("Error"));

        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage().contains("A class jakarta.validation.ValidationException occurred handling request GET /test: HttpStatus 400 - Error"));
    }
}