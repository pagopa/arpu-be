package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.citizen.client.OrganizationClient;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CitizenServiceImplTest {

    @Mock
    private OrganizationClient organizationClientMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    CitizenService citizenService;

    @BeforeEach
    void setUp() {
        citizenService = new CitizenServiceImpl(organizationClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationClientMock
        );
    }

    @Test
    void givenBrokerIdWhenGetOrganizationsWithSpontaneousDTOThenReturnOrganizationsWithSpontaneousDTO() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);

        Mockito.when(organizationClientMock.getOrganizationsWithSpontaneousDTO(accessToken, brokerId)).thenReturn(expectedResult);
        //when
        List<OrganizationsWithSpontaneousDTO> result = citizenService.getOrganizationsWithSpontaneousDTO(accessToken, brokerId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}