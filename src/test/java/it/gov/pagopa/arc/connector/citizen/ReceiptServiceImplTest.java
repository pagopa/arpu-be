package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.ReceiptClient;
import it.gov.pagopa.arc.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceImplTest {

    @Mock
    private ReceiptClient receiptClientMock;
    @Mock
    private AuthnService authnServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptServiceImpl(receiptClientMock, authnServiceMock);
    }

    @AfterEach
    void mockitoVerify() {
        Mockito.verifyNoMoreInteractions(receiptClientMock, authnServiceMock);
    }

    @Test
    void givenFiltersWhenGetPagedDebtorReceiptsThenPagedDebtorReceiptsDTO() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String fiscalCode = "fiscalCode";
        DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO = podamFactory.manufacturePojo(DebtorReceiptsFiltersDTO.class);
        PageRequest pageRequest = PageRequest.of(1, 10);
        PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(receiptClientMock.getPagedDebtorReceipts(brokerId, fiscalCode, debtorReceiptsFiltersDTO, pageRequest, accessToken)).thenReturn(expectedResult);
        //when
        PagedDebtorReceiptsDTO result = receiptService.getPagedDebtorReceipts(brokerId, fiscalCode, debtorReceiptsFiltersDTO, pageRequest);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void whenGetReceiptDetailThenInvokeClient() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";
        ReceiptDetailExtendedDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailExtendedDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(receiptClientMock.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,accessToken)).thenReturn(expectedResult);

        ReceiptDetailExtendedDTO result = receiptService.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void whenGetReceiptPdfThenInvokeClient() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";
        FileResourceDTO expectedResult = podamFactory.manufacturePojo(FileResourceDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(receiptClientMock.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,accessToken)).thenReturn(expectedResult);

        FileResourceDTO result = receiptService.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void whenGetDebtorReceiptsThenInvokeClient() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long debtPositionId = 3L;
        Long paymentOptionId = 4L;
        String debtorFiscalCode = "debtorFiscalCode";
        List<DebtorReceiptDTO> expectedResult = podamFactory.manufacturePojo(List.class,DebtorReceiptDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(receiptClientMock.getDebtorReceipts(debtorFiscalCode,brokerId,organizationId,debtPositionId,paymentOptionId,accessToken)).thenReturn(expectedResult);

        List<DebtorReceiptDTO> result = receiptService.getDebtorReceipts(debtorFiscalCode,brokerId,organizationId,debtPositionId,paymentOptionId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }
}