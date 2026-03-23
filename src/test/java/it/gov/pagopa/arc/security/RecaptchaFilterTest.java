package it.gov.pagopa.arc.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.GoogleRecaptchaService;
import it.gov.pagopa.arc.model.generated.ErrorDTO;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static it.gov.pagopa.arc.security.RecaptchaFilter.MISSING_RECAPTCHA_HEADER_ERROR_CODE;
import static it.gov.pagopa.arc.security.RecaptchaFilter.X_RECAPTCHA_TOKEN;

@ExtendWith(MockitoExtension.class)
class RecaptchaFilterTest {
  @Mock
  private GoogleRecaptchaService googleRecaptchaServiceMock;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private RecaptchaFilter recaptchaFilter;
  private final String protectedPath = "/path/mock";

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    recaptchaFilter = new RecaptchaFilter(List.of(protectedPath),googleRecaptchaServiceMock,objectMapper);
  }
  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
    Mockito.verifyNoMoreInteractions(googleRecaptchaServiceMock);
  }

  @Test
  void givenValidRequestWhenDoFilterInternalThenRequestForwarded() throws ServletException, IOException {
    String recaptchaToken = "recaptchaToken";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(X_RECAPTCHA_TOKEN,recaptchaToken);
    MockHttpServletResponse response = new MockHttpServletResponse();
    response.setStatus(200);

    Mockito.when(googleRecaptchaServiceMock.isVerified(recaptchaToken)).thenReturn(true);

    recaptchaFilter.doFilterInternal(request,response,new MockFilterChain());

    Assertions.assertEquals(200, response.getStatus());
  }

  @Test
  void givenInvalidRequestWhenDoFilterInternalThen401() throws ServletException, IOException {
    String recaptchaToken = "recaptchaToken";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(X_RECAPTCHA_TOKEN,recaptchaToken);
    MockHttpServletResponse response = new MockHttpServletResponse();
    response.setStatus(200);

    Mockito.when(googleRecaptchaServiceMock.isVerified(recaptchaToken)).thenReturn(false);

    recaptchaFilter.doFilterInternal(request,response,new MockFilterChain());

    assertErrorResponse(response);
  }

  @Test
  void givenNoTokenWhenDoFilterInternalThen401() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    response.setStatus(200);

    recaptchaFilter.doFilterInternal(request,response,new MockFilterChain());

    assertErrorResponse(response);
  }

  private void assertErrorResponse(MockHttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
    Assertions.assertEquals(401, response.getStatus());
    Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

    ErrorDTO errorDTO = objectMapper.readValue(response.getContentAsString(),ErrorDTO.class);
    Assertions.assertEquals(ErrorDTO.CategoryEnum.UNAUTHORIZED,errorDTO.getCategory());
    Assertions.assertEquals(MISSING_RECAPTCHA_HEADER_ERROR_CODE,errorDTO.getCode());
    Assertions.assertEquals("Invalid or missing reCAPTCHA token",errorDTO.getMessage());
  }

  @Test
  void givenMatchingPathWhenShouldNotFilterThenFalse() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI(protectedPath);

    boolean result = recaptchaFilter.shouldNotFilter(request);

    Assertions.assertFalse(result);
  }

  @Test
  void givenNoMatchingPathWhenShouldNotFilterThenTrue() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setRequestURI("/path/wrong");

    boolean result = recaptchaFilter.shouldNotFilter(request);

    Assertions.assertTrue(result);
  }
}