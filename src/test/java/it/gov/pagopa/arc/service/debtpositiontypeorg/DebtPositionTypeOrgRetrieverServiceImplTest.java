package it.gov.pagopa.arc.service.debtpositiontypeorg;

import it.gov.pagopa.arc.connector.citizen.DebtPositionTypeOrgService;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgRetrieverServiceImplTest {

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

    @BeforeEach
    void setUp() {
       debtPositionTypeOrgRetrieverService = new DebtPositionTypeOrgRetrieverServiceImpl(debtPositionTypeOrgServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
               debtPositionTypeOrgServiceMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenReturnDebtPositionTypeOrgsWithSpontaneous() {
        Long organizationId = 1L;
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgsWithSpontaneous(organizationId)).thenReturn(expectedResult);
        //when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(organizationId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}