package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.receipt.ReceiptFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtilsTest;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

    @Mock
    private ReceiptFacadeService receiptFacadeServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();
    private final IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

    ReceiptController receiptController;

    @BeforeEach
    void setUp() {
        SecurityUtilsTest.configureSecurityContext(loggedUser);
        receiptController = new ReceiptController(receiptFacadeServiceMock);
    }

    @AfterEach
    void mockitoVerify() {
        Mockito.verifyNoMoreInteractions(receiptFacadeServiceMock);
        SecurityUtilsTest.clearSecurityContext();
    }

    @Test
    void givenFiltersWhenGetPagedDebtorReceiptsThenOk() {
        //given

        Long brokerId = 1L;
        String fiscalCode = "fiscalCode";
        String orgName = "orgName";
        PageRequest pageRequest = PageRequest.of(1, 10);
        PagedDebtorReceiptsDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorReceiptsDTO.class);

        Mockito.when(receiptFacadeServiceMock.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest, loggedUser)).thenReturn(expectedResult);
        //when
        ResponseEntity<PagedDebtorReceiptsDTO> result = receiptController.getPagedDebtorReceipts(brokerId, fiscalCode, orgName, pageRequest);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }
}