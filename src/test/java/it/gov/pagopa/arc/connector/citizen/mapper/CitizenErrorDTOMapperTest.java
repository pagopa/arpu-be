package it.gov.pagopa.arc.connector.citizen.mapper;

import it.gov.pagopa.pu.citizen.dto.generated.ErrorDTO;
import it.gov.pagopa.arc.config.rest.PuErrorDTO;
import it.gov.pagopa.arc.model.generated.ErrorFieldDTO;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

class CitizenErrorDTOMapperTest {

  @Test
  void whenMapThenReturnPuErrorDTO() {
    // Given
    ErrorDTO errorDTO = TestUtils.getPodamFactory().manufacturePojo(ErrorDTO.class);
    Objects.requireNonNull(errorDTO.getFields());

    // When
    PuErrorDTO result = CitizenErrorDTOMapper.map(errorDTO);

    // Then
    TestUtils.checkNotNullFields(result);

    Assertions.assertEquals(errorDTO.getCategory().getValue(), result.category());
    Assertions.assertEquals(errorDTO.getCode(), result.code());
    Assertions.assertEquals(errorDTO.getMessage(), result.message());

    Assertions.assertEquals(5, result.fields().size());
    List<ErrorFieldDTO> fields = result.fields();
    for (int i = 0; i < fields.size(); i++) {
      ErrorFieldDTO ef = fields.get(i);
      TestUtils.checkNotNullFields(ef);

      it.gov.pagopa.pu.citizen.dto.generated.ErrorFieldDTO expectedErrorFieldDTO = errorDTO.getFields().get(i);
      Assertions.assertEquals(expectedErrorFieldDTO.getField(), ef.getField());
      Assertions.assertEquals(expectedErrorFieldDTO.getError(), ef.getError());
      Assertions.assertEquals(expectedErrorFieldDTO.getMessage(), ef.getMessage());
    }

  }

}
