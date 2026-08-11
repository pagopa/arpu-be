package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.client.generated.OrganizationApi;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizationClientTest {
    @Mock
    private CitizenApisHolder citizenApisHolderMock;
    @Mock
    private OrganizationApi organizationApiMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private OrganizationClient organizationClient;

    @BeforeEach
    void setUp() {
        organizationClient = new OrganizationClient(citizenApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                citizenApisHolderMock,
                organizationApiMock
        );
    }

    @Test
    void givenBrokerIdWhenGetOrganizationsWithSpontaneousDTOThenReturnOrganizationsWithSpontaneousDTO(){
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);

        Mockito.when(citizenApisHolderMock.getOrganizationApi(accessToken)).thenReturn(organizationApiMock);
        Mockito.when(organizationApiMock.getOrganizationsWithSpontaneous(brokerId)).thenReturn(expectedResult);
        //when
        List<OrganizationsWithSpontaneousDTO> result = organizationClient.getOrganizationsWithSpontaneousDTO(accessToken, brokerId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenGetOrganizationLogoThenOk() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String orgFiscalCode = "orgFiscalCode";

        OrganizationLogoDTO expectedResult = new OrganizationLogoDTO();

        Mockito.when(citizenApisHolderMock.getOrganizationApi(accessToken)).thenReturn(organizationApiMock);
        Mockito.when(organizationApiMock.getOrganizationLogo(brokerId, orgFiscalCode)).thenReturn(expectedResult);
        //when
        OrganizationLogoDTO result = organizationClient.getOrganizationLogo(brokerId, orgFiscalCode, accessToken);
        //then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundExceptionWhenGetOrganizationLogoThenReturnNull() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String orgFiscalCode = "orgFiscalCode";

        Mockito.when(citizenApisHolderMock.getOrganizationApi(accessToken)).thenReturn(organizationApiMock);
        Mockito.when(organizationApiMock.getOrganizationLogo(brokerId, orgFiscalCode))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        //when
        OrganizationLogoDTO result = organizationClient.getOrganizationLogo(brokerId, orgFiscalCode, accessToken);
        //then
        assertNull(result);
    }
}