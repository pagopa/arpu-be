package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.InstallmentApi;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.installment.InstallmentFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtilsTest;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
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
class InstallmentControllerImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private InstallmentFacadeService installmentFacadeServiceMock;
    private InstallmentApi installmentController;
    private final IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

    @BeforeEach
    void setUp() {
        SecurityUtilsTest.configureSecurityContext(loggedUser);
        installmentController = new InstallmentControllerImpl(installmentFacadeServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                installmentFacadeServiceMock
        );
        SecurityUtilsTest.clearSecurityContext();
    }

    @AfterEach
    void clearContext() {
        SecurityUtilsTest.clearSecurityContext();
    }

    @Test
    void whenGetPublicInstallmentsByIuvOrNavThenOk() {
        Long brokerId = 1L;
        String iuvOrNav = "iuvOrNav";
        String debtorFiscalCode = "debtorFiscalCode";
        String orgFiscalCode = "orgFiscalCode";

        List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

        Mockito.when(installmentFacadeServiceMock.getPublicInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode))
                .thenReturn(expectedResult);

        ResponseEntity<List<InstallmentDebtorExtendedDTO>> response = installmentController.getPublicInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResult, response.getBody());
    }

    @Test
    void whenGetDebtorUnpaidDebtPositionInstallmentsThenOk() {
        Long brokerId = 1L;
        Long debtPositionId = 10L;
        Long paymentOptionId = 20L;
        String xFiscalCode = "XYZABC12D34E567F";
        Long organizationId = 100L;

        List<DebtorUnpaidDebtPositionInstallmentsDTO> expectedResult =
                podamFactory.manufacturePojo(List.class, DebtorUnpaidDebtPositionInstallmentsDTO.class);

        Mockito.when(installmentFacadeServiceMock.getDebtorUnpaidDebtPositionInstallments(
                        brokerId, debtPositionId, paymentOptionId, xFiscalCode, organizationId))
                .thenReturn(expectedResult);

        ResponseEntity<List<DebtorUnpaidDebtPositionInstallmentsDTO>> response =
                installmentController.getDebtorUnpaidDebtPositionInstallments(
                        brokerId, debtPositionId, paymentOptionId, xFiscalCode, organizationId
                );

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResult, response.getBody());
    }
}