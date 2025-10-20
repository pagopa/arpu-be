package it.gov.pagopa.arc.service.organization;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.OrganizationService;
import it.gov.pagopa.arc.utils.TestUtils;
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
class OrganizationRetrieveServiceImplTest {

    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private AuthnService authnServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    OrganizationRetrieveService organizationRetrieveService;

    @BeforeEach
    void setUp() {
        organizationRetrieveService = new OrganizationRetrieveServiceImpl(organizationServiceMock, authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationServiceMock, authnServiceMock
        );
    }

    @Test
    void givenBrokerIdWhenGetOrganizationsWithSpontaneousDTOThenReturnOrganizationsWithSpontaneousDTO() {
        //given
        Long brokerId = 1L;
        String accessToken = "accessToken";
        List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);

        Mockito.when(organizationServiceMock.getOrganizationsWithSpontaneousDTO(accessToken, brokerId)).thenReturn(expectedResult);
        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        //when
        List<OrganizationsWithSpontaneousDTO> result = organizationRetrieveService.getOrganizationsWithSpontaneousDTO(brokerId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}