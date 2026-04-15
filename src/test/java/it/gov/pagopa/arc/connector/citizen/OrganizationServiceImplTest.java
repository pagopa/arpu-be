package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.OrganizationClient;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
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
class OrganizationServiceImplTest {

    @Mock
    private OrganizationClient organizationClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        organizationService = new OrganizationServiceImpl(organizationClientMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationClientMock,
                authnServiceMock
        );
    }

    @Test
    void givenBrokerIdWhenGetOrganizationsWithSpontaneousDTOThenReturnOrganizationsWithSpontaneousDTO() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(organizationClientMock.getOrganizationsWithSpontaneousDTO(accessToken, brokerId)).thenReturn(expectedResult);
        //when
        List<OrganizationsWithSpontaneousDTO> result = organizationService.getOrganizationsWithSpontaneousDTO(brokerId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenGetOrganizationLogoThenInvokeClient() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String orgFiscalCode = "orgFiscalCode";
        OrganizationLogoDTO expectedResult = podamFactory.manufacturePojo(OrganizationLogoDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(organizationClientMock.getOrganizationLogo(brokerId, orgFiscalCode, accessToken)).thenReturn(expectedResult);
        //when
        OrganizationLogoDTO result = organizationService.getOrganizationLogo(brokerId,orgFiscalCode);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}