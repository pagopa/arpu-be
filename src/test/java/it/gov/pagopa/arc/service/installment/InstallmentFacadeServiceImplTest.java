package it.gov.pagopa.arc.service.installment;

import it.gov.pagopa.arc.connector.citizen.InstallmentService;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
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
class InstallmentFacadeServiceImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private InstallmentService installmentServiceMock;
    private InstallmentFacadeService installmentFacadeService;

    @BeforeEach
    void setUp() {
        installmentFacadeService = new InstallmentFacadeServiceImpl(installmentServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(installmentServiceMock);
    }

    @Test
    void whenGetPublicInstallmentsByIuvOrNavThenOk() {
        Long brokerId = 1L;
        String iuvOrNav = "iuvOrNav";
        String debtorFiscalCode = "debtorFiscalCode";
        String orgFiscalCode = "orgFiscalCode";
        List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

        Mockito.when(installmentServiceMock.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode)).thenReturn(expectedResult);

        List<InstallmentDebtorExtendedDTO> result = installmentFacadeService.getPublicInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}