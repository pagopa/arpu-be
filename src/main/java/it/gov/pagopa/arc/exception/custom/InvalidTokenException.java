package it.gov.pagopa.arc.exception.custom;

import it.gov.pagopa.arc.exception.common.BaseBusinessException;
import lombok.Getter;

@Getter
public class InvalidTokenException extends BaseBusinessException {

  public InvalidTokenException(String message) {
    super("INVALID_TOKEN", message);
  }
}