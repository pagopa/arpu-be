package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
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
class DebtPositionTypeOrgServiceImplTest {

    @Mock
    private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgService debtPositionTypeOrgService;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgService = new DebtPositionTypeOrgServiceImpl(debtPositionTypeOrgClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgClientMock,
                authnServiceMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenDebtPositionTypeOrgsWithSpontaneous() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionTypeOrgClientMock.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken)).thenReturn(expectedResult);
        //when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgService.getDebtPositionTypeOrgsWithSpontaneous(organizationId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void givenOrganizationIdAndDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenReturnDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionTypeOrgClientMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId, accessToken)).thenReturn(expectedResult);
        //when
        DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgService.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

}