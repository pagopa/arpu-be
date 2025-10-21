package it.gov.pagopa.arc.service.debtpositiontypeorg;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
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
    @Mock
    private AuthnService authnServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

    @BeforeEach
    void setUp() {
       debtPositionTypeOrgRetrieverService = new DebtPositionTypeOrgRetrieverServiceImpl(debtPositionTypeOrgServiceMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
               debtPositionTypeOrgServiceMock,
                authnServiceMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenReturnDebtPositionTypeOrgsWithSpontaneous() {
        Long organizationId = 1L;
        String accessToken = "accessToken";
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken)).thenReturn(expectedResult);
        //when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(organizationId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}