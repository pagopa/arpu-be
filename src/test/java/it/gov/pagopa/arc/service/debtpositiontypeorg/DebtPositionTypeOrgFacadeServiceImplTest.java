package it.gov.pagopa.arc.service.debtpositiontypeorg;

import it.gov.pagopa.arc.connector.citizen.DebtPositionTypeOrgService;
import it.gov.pagopa.arc.exception.custom.ResourceNotFoundException;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
class DebtPositionTypeOrgFacadeServiceImplTest {

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgFacadeService debtPositionTypeOrgFacadeService;

    @BeforeEach
    void setUp() {
       debtPositionTypeOrgFacadeService = new DebtPositionTypeOrgFacadeServiceImpl(debtPositionTypeOrgServiceMock);
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
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgFacadeService.getDebtPositionTypeOrgsWithSpontaneous(organizationId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void givenOrganizationIdAndDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenReturnDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
        //given
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId)).thenReturn(expectedResult);
        //when
        DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgFacadeService.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void givenNullOrganizationIdAndDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenThrowException() {
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId)).thenReturn(null);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> debtPositionTypeOrgFacadeService.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId));
        Assertions.assertEquals("DebtPositionTypeOrgsWithSpontaneousDetails with deptPositionTypeOrgId 1 brokerId 1 and organizationId 1 not found", ex.getMessage());
    }
}