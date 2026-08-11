package it.gov.pagopa.arc.exception.transcoder.handler;

import it.gov.pagopa.arc.exception.common.BaseBusinessException;
import it.gov.pagopa.arc.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.arc.exception.transcoder.ExceptionMessageTranscoder;

public class BaseBusinessExceptionMessageTranscoder implements ExceptionMessageTranscoder<BaseBusinessException> {
  @Override
  public ExceptionMessageTranscoded transcode(BaseBusinessException businessException) {
    return new ExceptionMessageTranscoded(businessException.getCode(), businessException.getMessage(), businessException.getFields());
  }
}
