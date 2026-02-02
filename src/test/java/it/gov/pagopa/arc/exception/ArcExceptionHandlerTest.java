package it.gov.pagopa.arc.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.arc.config.json.JsonConfig;
import it.gov.pagopa.arc.dto.mapper.UpstreamErrorMapper;
import it.gov.pagopa.arc.exception.custom.*;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.arc.utils.UtilitiesTest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@WebMvcTest(value = {ArcExceptionHandlerTest.TestController.class},
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class
        })
@ContextConfiguration(classes = {
    ArcExceptionHandlerTest.TestController.class,
    ArcExceptionHandler.class,
    JsonConfig.class})
@AutoConfigureMockMvc(addFilters = false)
class ArcExceptionHandlerTest {

    public static final String DATA = "data";
    public static final String HEADER = "header";
    private static final String TRACE_ID = "TRACEID";
    public static final TestRequestBody BODY = new TestRequestBody("bodyData", null, "abc", LocalDateTime.now());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoSpyBean
    private TestController testControllerSpy;

    @MockitoBean
    UpstreamErrorMapper upstreamErrorMapperMock;

    @BeforeEach
    void setTraceId(){
        UtilitiesTest.setTraceId(TRACE_ID);
    }
    @AfterEach
    void clearTraceId(){
        UtilitiesTest.clearTraceIdContext();
    }

    @RestController
    @Slf4j
    static class TestController {
        @PostMapping(value = "/test", produces = MediaType.APPLICATION_JSON_VALUE)
        String testEndpoint(@RequestParam(DATA) String data, @Valid @RequestBody TestRequestBody body) {
            return "OK";
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestRequestBody {
        @NotNull
        private String requiredField;
        private String notRequiredField;
        @Pattern(regexp = "[a-z]+")
        private String lowerCaseAlphabeticField;
        private LocalDateTime dateTimeField;
    }

    @BeforeEach
    void init() {
        TestUtils.clearDefaultTimezone();
    }

    private ResultActions performRequest(String data, MediaType accept) throws Exception {
        return performRequest(data, accept, objectMapper.writeValueAsString(ArcExceptionHandlerTest.BODY));
    }

    private ResultActions performRequest(String data, MediaType accept, String body) throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/test")
                .param(DATA, data)
                .accept(accept);

        if (body != null) {
            requestBuilder
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body);
        }

        return mockMvc.perform(requestBuilder);
    }

     @Test
    void givenInvalidTokenThenHandleInvalidTokenException() throws Exception {
        doThrow(new InvalidTokenException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

         performRequest(DATA, MediaType.APPLICATION_JSON)
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("UNAUTHORIZED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }

    @Test
    void givenInvalidEmailWhenExtractNameFromEmailAssistanceTokenThenHandleZendeskAssistanceInvalidUserEmailException() throws Exception {
        doThrow(new ZendeskAssistanceInvalidUserEmailException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }

    @Test
    void givenGenericErrorThenThrowException() throws Exception {
        doThrow(new RuntimeException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
            .andExpect(MockMvcResultMatchers.status().is(500))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("GENERIC_ERROR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Error"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("GENERIC_ERROR"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }

    private final ConstraintViolationException constraintViolationException = new ConstraintViolationException("Error", Set.of(ConstraintViolationImpl.forParameterValidation(
            "error message template", Map.of(), Map.of(), "resolved message", null, null, null, null, PathImpl.createPathFromString("fieldName"), null, null, null
    )));

    @Test
    void handleViolationException() throws Exception {
        doThrow(constraintViolationException).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Invalid request content. fieldName: resolved message"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }

    @Test
    void handleHttpMessageNotReadableException() throws Exception {
        doThrow(new HttpMessageNotReadableException("Error", null)).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Required request body is missing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }


    @Test
    void givenHttpClientErrorExceptionWithInvalidHttpStatusWhenRequestThenHandleHttpClientErrorException() throws Exception {
        String upstreamBody = """
          {"code":"SOME_UPSTREAM_CODE","message":"[INVALID_IBAN] eltjhreigjpo","traceId":"slfjhdio"}
          """;

        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Error",
                HttpHeaders.EMPTY,
                upstreamBody.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        when(upstreamErrorMapperMock.from(any(HttpClientErrorException.class)))
                .thenReturn(new UpstreamErrorMapper.MappedUpstreamError(
                        "INVALID_IBAN",
                        "eltjhreigjpo"
                ));

        doThrow(ex).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("INVALID_IBAN"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("eltjhreigjpo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }

    @Test
    void givenValidationErrorWhenRequestThenHandleValidationException() throws Exception {
        doThrow(new ValidationException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("GENERIC_ERROR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }

    @Test
    void givenResourceNotFoundErrorWhenRequestThenHandleResourceNotFoundException() throws Exception {
        doThrow(new ResourceNotFoundException("RESOURCE_NOT_FOUND","Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

        performRequest(DATA, MediaType.APPLICATION_JSON)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(TRACE_ID));
    }
}