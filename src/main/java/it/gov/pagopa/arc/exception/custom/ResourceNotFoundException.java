package it.gov.pagopa.arc.exception.custom;

public class ResourceNotFoundException extends BaseBusinessException {
  public ResourceNotFoundException(String code, String message) {
    super(code, message);
  }
}

