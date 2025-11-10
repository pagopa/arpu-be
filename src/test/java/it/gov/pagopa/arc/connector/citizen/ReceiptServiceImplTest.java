package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.ReceiptClient;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailDTO;
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
        String orgName = "orgName";
        PageRequest pageRequest = PageRequest.of(1, 10);
        PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(receiptClientMock.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest, accessToken)).thenReturn(expectedResult);
        //when
        PagedDebtorReceiptsDTO result = receiptService.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest);
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
        ReceiptDetailDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(receiptClientMock.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,accessToken)).thenReturn(expectedResult);

        ReceiptDetailDTO result = receiptService.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }
}