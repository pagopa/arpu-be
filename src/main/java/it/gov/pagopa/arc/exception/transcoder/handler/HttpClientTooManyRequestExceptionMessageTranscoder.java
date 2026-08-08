package it.gov.pagopa.arc.exception.transcoder.handler;

import it.gov.pagopa.arc.model.generated.ErrorDTO;
import it.gov.pagopa.arc.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.arc.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.web.client.HttpClientErrorException;

public class HttpClientTooManyRequestExceptionMessageTranscoder implements ExceptionMessageTranscoder<HttpClientErrorException.TooManyRequests> {
  @Override
  public ExceptionMessageTranscoded transcode(HttpClientErrorException.TooManyRequests tooManyRequestsException) {
    return new ExceptionMessageTranscoded(
      ErrorDTO.CategoryEnum.TOO_MANY_REQUESTS.name(),
      tooManyRequestsException.getMessage(),
      null);
  }
}
