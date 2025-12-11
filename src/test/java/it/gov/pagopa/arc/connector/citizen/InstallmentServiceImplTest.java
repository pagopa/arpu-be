package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.InstallmentClient;
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
class InstallmentServiceImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private InstallmentClient installmentClientMock;
    private InstallmentService installmentService;

    @BeforeEach
    void setUp() {
         installmentService = new InstallmentServiceImpl(authnServiceMock, installmentClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                installmentClientMock
        );
    }

    @Test
    void whenGetInstallmentsByIuvOrNavThenInvokeClient() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String iuvOrNav = "iuvOrNav";
        String debtorFiscalCode = "debtorFiscalCode";
        String orgFiscalCode = "orgFiscalCode";
        List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(installmentClientMock.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode,accessToken))
                .thenReturn(expectedResult);

        List<InstallmentDebtorExtendedDTO> result = installmentService.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}