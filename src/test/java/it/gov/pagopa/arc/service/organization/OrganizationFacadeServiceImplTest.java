package it.gov.pagopa.arc.service.organization;

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
class OrganizationFacadeServiceImplTest {

    @Mock
    private OrganizationService organizationServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    OrganizationFacadeService organizationFacadeService;

    @BeforeEach
    void setUp() {
        organizationFacadeService = new OrganizationFacadeServiceImpl(organizationServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationServiceMock
        );
    }

    @Test
    void givenBrokerIdWhenGetOrganizationsWithSpontaneousDTOThenReturnOrganizationsWithSpontaneousDTO() {
        //given
        Long brokerId = 1L;

        List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);

        Mockito.when(organizationServiceMock.getOrganizationsWithSpontaneousDTO(brokerId)).thenReturn(expectedResult);
        //when
        List<OrganizationsWithSpontaneousDTO> result = organizationFacadeService.getOrganizationsWithSpontaneousDTO(brokerId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}