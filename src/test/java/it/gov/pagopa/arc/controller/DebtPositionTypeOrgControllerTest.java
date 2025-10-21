package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgControllerTest {

    @Mock
    private DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgController debtPositionTypeOrgController;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgController = new DebtPositionTypeOrgController(debtPositionTypeOrgRetrieverServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgRetrieverServiceMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenOk() {
        //given
        Long organizationId = 1L;
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
        Mockito.when(debtPositionTypeOrgRetrieverServiceMock.getDebtPositionTypeOrgsWithSpontaneous(organizationId)).thenReturn(expectedResult);
        //when
        ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result = debtPositionTypeOrgController.getDebtPositionTypeOrgsWithSpontaneous(organizationId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }
}