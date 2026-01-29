package it.gov.pagopa.arc.dto.mapper;

import it.gov.pagopa.arc.dto.UpstreamErrorDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class UpstreamErrorMapper {

  private final JsonMapper jsonMapper;

  public UpstreamErrorMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
  }

  public record MappedUpstreamError(String code, String description) {
  }

  public MappedUpstreamError from(HttpClientErrorException ex) {
    UpstreamErrorDTO upstream = tryParse(ex);

    String fallbackMessage = ex.getMessage();
    String genericErrorCode = "GENERIC_ERROR";
    String upstreamDescription = upstream != null ? upstream.getDescription() : null;
    String upstreamCode = upstream != null ? upstream.getCode() : null;

    String resolvedMessage = StringUtils.firstNonBlank(upstreamDescription, fallbackMessage);
    String resolvedCode = StringUtils.firstNonBlank(upstreamCode, genericErrorCode);

    return new MappedUpstreamError(resolvedCode, resolvedMessage);
  }

  private UpstreamErrorDTO tryParse(HttpClientErrorException ex) {
    try {
      String body = ex.getResponseBodyAsString();
      if (StringUtils.isBlank(body)) {
        return null;
      }
      return jsonMapper.readValue(body, UpstreamErrorDTO.class);
    } catch (Exception e) {
      return null;
    }
  }
}


