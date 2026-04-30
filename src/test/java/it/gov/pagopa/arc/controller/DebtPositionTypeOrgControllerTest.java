package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.service.debtpositiontypeorg.DebtPositionTypeOrgFacadeService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgControllerTest {

    @Mock
    private DebtPositionTypeOrgFacadeService debtPositionTypeOrgFacadeServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgController debtPositionTypeOrgController;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgController = new DebtPositionTypeOrgController(debtPositionTypeOrgFacadeServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgFacadeServiceMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenOk() {
        //given
        Long organizationId = 1L;
        Long brokerId = 1L;
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
        Mockito.when(debtPositionTypeOrgFacadeServiceMock.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId)).thenReturn(expectedResult);
        //when
        ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result = debtPositionTypeOrgController.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void givenOrganizationIdAndDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenReturnDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
        //given
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);
        Mockito.when(debtPositionTypeOrgFacadeServiceMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId)).thenReturn(expectedResult);
        //when
        ResponseEntity<DebtPositionTypeOrgsWithSpontaneousDetailsDTO> result = debtPositionTypeOrgController.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void givenOrganizationIdWhenGetPublicDebtPositionTypeOrgsWithSpontaneousThenOk() {
        //given
        Long brokerId = 1L;
        Long organizationId = 1L;
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
        Mockito.when(debtPositionTypeOrgFacadeServiceMock.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId)).thenReturn(expectedResult);
        //when
        ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result = debtPositionTypeOrgController.getPublicDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void givenOrganizationIdAndDebtPositionTypeOrgIdWhenGetPublicDebtPositionTypeOrgsWithSpontaneousDetailThenReturnDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
        //given
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);
        Mockito.when(debtPositionTypeOrgFacadeServiceMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId)).thenReturn(expectedResult);
        //when
        ResponseEntity<DebtPositionTypeOrgsWithSpontaneousDetailsDTO> result = debtPositionTypeOrgController.getPublicDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void givenOrganizationIdWhenGetMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYearThenOk() {
        // given
        Long brokerId = 1L;
        Long organizationId = 1L;

        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult =
                podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(
                debtPositionTypeOrgFacadeServiceMock.getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(
                        brokerId,
                        organizationId
                )
        ).thenReturn(expectedResult);

        // when
        ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result =
                debtPositionTypeOrgController.getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(brokerId, organizationId);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void givenOrganizationIdWhenGetPublicMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYearThenOk() {
        // given
        Long brokerId = 1L;
        Long organizationId = 1L;

        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult =
                podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(
                debtPositionTypeOrgFacadeServiceMock.getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(
                        brokerId,
                        organizationId
                )
        ).thenReturn(expectedResult);

        // when
        ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> result =
                debtPositionTypeOrgController.getPublicMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(brokerId, organizationId);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }
}