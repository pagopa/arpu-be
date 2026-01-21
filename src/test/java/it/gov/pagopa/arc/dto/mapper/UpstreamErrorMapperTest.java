package it.gov.pagopa.arc.dto.mapper;

import it.gov.pagopa.arc.dto.UpstreamErrorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpstreamErrorMapperTest {

  @Mock
  private JsonMapper jsonMapper;

  private UpstreamErrorMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new UpstreamErrorMapper(jsonMapper);
  }

  @Test
  void givenErrorDTOResponseWhenFromThenExtractUpstreamCodeAndDescription() {
    // given
    String body = """
      {"code":"UPSTREAM_CODE","description":"eltjhreigjpo","traceId":"t1"}
      """;

    HttpClientErrorException ex = HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            HttpHeaders.EMPTY,
            body.getBytes(StandardCharsets.UTF_8),
            StandardCharsets.UTF_8
    );

    UpstreamErrorDTO dto = new UpstreamErrorDTO();
    dto.setCode("UPSTREAM_CODE");
    dto.setDescription("eltjhreigjpo");

    when(jsonMapper.readValue(body, UpstreamErrorDTO.class))
            .thenReturn(dto);

    // when
    UpstreamErrorMapper.MappedUpstreamError mapped = mapper.from(ex);

    // then
    assertNotNull(mapped);
    assertEquals("UPSTREAM_CODE", mapped.code());
    assertEquals("eltjhreigjpo", mapped.description());

    verify(jsonMapper).readValue(body, UpstreamErrorDTO.class);
    verifyNoMoreInteractions(jsonMapper);
  }

  @Test
  void givenParsableBodyWithoutBracketCodeWhenFromThenGenericCodeAndDescriptionIsMessage() {
    // given
    String body = """
      {"code":"UPSTREAM_CODE","message":"Just a plain message","traceId":"t1"}
      """;

    HttpClientErrorException ex = HttpClientErrorException.create(
      HttpStatus.BAD_REQUEST,
      "Bad Request",
      HttpHeaders.EMPTY,
      body.getBytes(StandardCharsets.UTF_8),
      StandardCharsets.UTF_8
    );

    UpstreamErrorDTO dto = new UpstreamErrorDTO();
      dto.setDescription("Just a plain message");

    when(jsonMapper.readValue(body, UpstreamErrorDTO.class))
      .thenReturn(dto);

    // when
    UpstreamErrorMapper.MappedUpstreamError mapped = mapper.from(ex);

    // then
    assertNotNull(mapped);
    assertEquals("GENERIC_ERROR", mapped.code());
    assertEquals("Just a plain message", mapped.description());

    verify(jsonMapper).readValue(body, UpstreamErrorDTO.class);
    verifyNoMoreInteractions(jsonMapper);
  }

  @Test
  void givenEmptyBodyWhenFromThenFallbackToExceptionMessageAndNoJsonRead() {
    // given
    HttpClientErrorException ex = HttpClientErrorException.create(
      HttpStatus.BAD_REQUEST,
      "Error",
      HttpHeaders.EMPTY,
      new byte[0],
      StandardCharsets.UTF_8
    );

    // when
    UpstreamErrorMapper.MappedUpstreamError mapped = mapper.from(ex);

    // then
    assertNotNull(mapped);
    assertEquals("GENERIC_ERROR", mapped.code());
    assertEquals(ex.getMessage(), mapped.description());

    verifyNoInteractions(jsonMapper);
  }

  @Test
  void givenJsonParsingFailsWhenFromThenFallbackToExceptionMessage() {
    // given
    String body = """
      {"code":"UPSTREAM_CODE","description":"eltjhreigjpo","traceId":"t1"}
      """;

    HttpClientErrorException ex = HttpClientErrorException.create(
      HttpStatus.BAD_REQUEST,
      "Error",
      HttpHeaders.EMPTY,
      body.getBytes(StandardCharsets.UTF_8),
      StandardCharsets.UTF_8
    );

    when(jsonMapper.readValue(body, UpstreamErrorDTO.class))
      .thenThrow(new RuntimeException("boom"));

    // when
    UpstreamErrorMapper.MappedUpstreamError mapped = mapper.from(ex);

    // then
    assertNotNull(mapped);
    assertEquals("GENERIC_ERROR", mapped.code());
    assertEquals(ex.getMessage(), mapped.description());

    verify(jsonMapper).readValue(body, UpstreamErrorDTO.class);
    verifyNoMoreInteractions(jsonMapper);
  }

  @Test
  void givenUpstreamDescriptionIsBlankWhenFromThenUseFallbackMessage() {
    // given
    String body = """
      {"code":"UPSTREAM_CODE","description":"   ","traceId":"t1"}
      """;

    HttpClientErrorException ex = HttpClientErrorException.create(
      HttpStatus.BAD_REQUEST,
      "Fallback error message",
      HttpHeaders.EMPTY,
      body.getBytes(StandardCharsets.UTF_8),
      StandardCharsets.UTF_8
    );

    UpstreamErrorDTO dto = new UpstreamErrorDTO();
    dto.setCode("UPSTREAM_CODE");
    dto.setDescription("   ");

    when(jsonMapper.readValue(body, UpstreamErrorDTO.class))
      .thenReturn(dto);

    // when
    UpstreamErrorMapper.MappedUpstreamError mapped = mapper.from(ex);

    // then
    assertNotNull(mapped);
    assertEquals("UPSTREAM_CODE", mapped.code());
    assertEquals(ex.getMessage(), mapped.description());

    verify(jsonMapper).readValue(body, UpstreamErrorDTO.class);
    verifyNoMoreInteractions(jsonMapper);
  }

  @Test
  void givenUpstreamCodeIsBlankWhenFromThenUseGenericError() {
    // given
    String body = """
      {"code":" ","description":"eltjhreigjpo","traceId":"t1"}
      """;

    HttpClientErrorException ex = HttpClientErrorException.create(
            HttpStatus.BAD_REQUEST,
            "Fallback error message",
            HttpHeaders.EMPTY,
            body.getBytes(StandardCharsets.UTF_8),
            StandardCharsets.UTF_8
    );

    UpstreamErrorDTO dto = new UpstreamErrorDTO();
    dto.setCode("   ");
    dto.setDescription("eltjhreigjpo");

    when(jsonMapper.readValue(body, UpstreamErrorDTO.class))
            .thenReturn(dto);

    // when
    UpstreamErrorMapper.MappedUpstreamError mapped = mapper.from(ex);

    // then
    assertNotNull(mapped);
    assertEquals("GENERIC_ERROR", mapped.code());
    assertEquals(dto.getDescription(), mapped.description());

    verify(jsonMapper).readValue(body, UpstreamErrorDTO.class);
    verifyNoMoreInteractions(jsonMapper);
  }
}

