package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.OrganizationApi;
import it.gov.pagopa.arc.service.organization.OrganizationFacadeService;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrganizationControllerImplTest {

    @Mock
    private OrganizationFacadeService organizationFacadeServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private OrganizationApi organizationApi;

    @BeforeEach
    void setUp() {
        organizationApi = new OrganizationControllerImpl(organizationFacadeServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                organizationFacadeServiceMock
        );
    }

    @Test
    void givenBrokerIdWhenGetOrganizationsWithSpontaneousThenOk() {
        //given
        Long brokerId = 1L;
        List<OrganizationsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, OrganizationsWithSpontaneousDTO.class);
        Mockito.when(organizationFacadeServiceMock.getOrganizationsWithSpontaneousDTO(brokerId)).thenReturn(expectedResult);
        //when
        ResponseEntity<List<OrganizationsWithSpontaneousDTO>> result = organizationApi.getOrganizationsWithSpontaneous(brokerId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }
}