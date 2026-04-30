package it.gov.pagopa.arc.exception.custom;

import lombok.Getter;

@Getter
public abstract class BaseBusinessException extends RuntimeException {

  protected final String code;

  protected BaseBusinessException(String code, String message) {
    super(message);
    this.code = code;
  }
}
