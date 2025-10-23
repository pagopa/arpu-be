package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;

import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.controller.generated.OrganizationApi;
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
}