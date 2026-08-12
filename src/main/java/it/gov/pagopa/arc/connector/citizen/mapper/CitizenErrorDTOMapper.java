package it.gov.pagopa.arc.connector.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.ErrorDTO;
import it.gov.pagopa.arc.config.rest.PuErrorDTO;
import it.gov.pagopa.arc.model.generated.ErrorFieldDTO;

public class CitizenErrorDTOMapper {

  private CitizenErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(ErrorDTO errorDTO) {
    return new PuErrorDTO(
      errorDTO.getCategory().getValue(),
      errorDTO.getCode(),
      errorDTO.getMessage(),
      errorDTO.getFields() != null
        ? errorDTO.getFields().stream()
        .map(field -> new ErrorFieldDTO(
          field.getField(),
          field.getError(),
          field.getMessage()
        ))
        .toList()
        : null
    );
  }
}
