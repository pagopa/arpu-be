package it.gov.pagopa.arc.config.rest;

import it.gov.pagopa.arc.model.generated.ErrorFieldDTO;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {
}
