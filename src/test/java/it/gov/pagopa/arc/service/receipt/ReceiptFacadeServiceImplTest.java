package it.gov.pagopa.arc.service.receipt;

import it.gov.pagopa.arc.connector.citizen.ReceiptService;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailDTO;
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
class ReceiptFacadeServiceImplTest {

    @Mock
    private ReceiptService receiptServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    ReceiptFacadeService receiptFacadeService;

    @BeforeEach
    void setUp() {
        receiptFacadeService = new ReceiptFacadeServiceImpl(receiptServiceMock);
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(receiptServiceMock);
    }

    @Test
    void givenFiltersWhenGetPagedDebtorReceiptsThenOk() {
        //given
        Long brokerId = 1L;
        String fiscalCode = "fiscalCode";
        String orgName = "orgName";
        PageRequest pageRequest = PageRequest.of(1, 10);
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

        Mockito.when(receiptServiceMock.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest)).thenReturn(expectedResult);
        //when
        PagedDebtorReceiptsDTO result = receiptFacadeService.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest, loggedUser);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenGetReceiptDetailThenOk() {
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        ReceiptDetailDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailDTO.class);

        Mockito.when(receiptServiceMock.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode)).thenReturn(expectedResult);

        ReceiptDetailDTO result = receiptFacadeService.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode, loggedUser);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}