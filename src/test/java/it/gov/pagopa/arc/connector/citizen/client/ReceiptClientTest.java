package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.PageUtils;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.controller.generated.ReceiptApi;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReceiptClientTest {

    @Mock
    private CitizenApisHolder citizenApisHolderMock;
    @Mock
    private ReceiptApi receiptApiMock;

    ReceiptClient receiptClient;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();


    @BeforeEach
    void setUp() {
        receiptClient = new ReceiptClient(citizenApisHolderMock);
    }

    @AfterEach
    void mockitoVerify() {
        Mockito.verifyNoMoreInteractions(citizenApisHolderMock);
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

        Mockito.when(citizenApisHolderMock.getReceiptApi(accessToken)).thenReturn(receiptApiMock);
        Mockito.when(receiptApiMock.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest.getPageNumber(), pageRequest.getPageSize(), PageUtils.getSortList(pageRequest))).thenReturn(expectedResult);
        //when
        PagedDebtorReceiptsDTO result = receiptClient.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest, accessToken);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}