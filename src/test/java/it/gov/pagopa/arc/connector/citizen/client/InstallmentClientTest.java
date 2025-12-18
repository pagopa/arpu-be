package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.controller.generated.InstallmentApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

@ExtendWith(MockitoExtension.class)
class InstallmentClientTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();
    @Mock
    private CitizenApisHolder citizenApisHolderMock;
    @Mock
    private InstallmentApi installmentApiMock;
    private InstallmentClient installmentClient;

    @BeforeEach
    void setUp() {
        installmentClient = new InstallmentClient(citizenApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                citizenApisHolderMock,
                installmentApiMock
        );
    }

    @Test
    void whenGetInstallmentsByIuvOrNavThenOk(){
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String iuvOrNav = "iuvOrNav";
        String debtorFiscalCode = "debtorFiscalCode";
        String orgFiscalCode = "orgFiscalCode";
        List<InstallmentDebtorExtendedDTO> expectedResult = podamFactory.manufacturePojo(List.class,InstallmentDebtorExtendedDTO.class);

        Mockito.when(citizenApisHolderMock.getInstallmentApi(accessToken)).thenReturn(installmentApiMock);
        Mockito.when(installmentApiMock.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode)).thenReturn(expectedResult);

        List<InstallmentDebtorExtendedDTO> response = installmentClient.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode,accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResult,response);
    }

    @Test
    void givenNotFoundWhenGetInstallmentsByIuvOrNavThenEmptyList(){
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String iuvOrNav = "iuvOrNav";
        String debtorFiscalCode = "debtorFiscalCode";
        String orgFiscalCode = "orgFiscalCode";

        Mockito.when(citizenApisHolderMock.getInstallmentApi(accessToken)).thenReturn(installmentApiMock);
        Mockito.when(installmentApiMock.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        List<InstallmentDebtorExtendedDTO> response = installmentClient.getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode,accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isEmpty());
    }

    @Test
    void whenGetDebtorUnpaidDebtPositionInstallmentsThenOk() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        Long debtPositionId = 10L;
        Long paymentOptionId = 20L;
        String xFiscalCode = "ABCDEF12G34H567I";
        Long organizationId = 100L;

        List<DebtorUnpaidDebtPositionInstallmentsDTO> expectedResult =
                podamFactory.manufacturePojo(List.class, DebtorUnpaidDebtPositionInstallmentsDTO.class);

        Mockito.when(citizenApisHolderMock.getInstallmentApi(accessToken)).thenReturn(installmentApiMock);
        Mockito.when(installmentApiMock.getDebtorUnpaidDebtPositionInstallments(
                brokerId, debtPositionId, paymentOptionId, xFiscalCode, organizationId
        )).thenReturn(expectedResult);

        List<DebtorUnpaidDebtPositionInstallmentsDTO> response =
                installmentClient.getDebtorUnpaidDebtPositionInstallments(
                        accessToken, brokerId, debtPositionId, paymentOptionId, xFiscalCode, organizationId
                );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResult, response);
    }

}