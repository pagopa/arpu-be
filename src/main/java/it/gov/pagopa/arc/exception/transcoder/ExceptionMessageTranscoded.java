package it.gov.pagopa.arc.exception.transcoder;

import it.gov.pagopa.arc.model.generated.ErrorFieldDTO;
import lombok.Data;

import java.util.List;

@Data
public class ExceptionMessageTranscoded {
  private final String code;
  private final String message;
  private final List<ErrorFieldDTO> fields;
}
