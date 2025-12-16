package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.utils.PageUtils;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.controller.generated.ReceiptApi;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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


    @Test
    void whenGetReceiptDetailThenInvokeWithAccessToken() {
        String accessToken = "ACCESSTOKEN";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";
        ReceiptDetailExtendedDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailExtendedDTO.class);

        when(citizenApisHolderMock.getReceiptApi(accessToken))
                .thenReturn(receiptApiMock);
        when(receiptApiMock.getReceiptDetail(fiscalCode,brokerId,organizationId,receiptId))
                .thenReturn(expectedResult);

        ReceiptDetailExtendedDTO result = receiptClient.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,accessToken);

        assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundWhenGetReceiptDetailThenReturnNull() {
        String accessToken = "ACCESSTOKEN";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";

        when(citizenApisHolderMock.getReceiptApi(accessToken))
                .thenReturn(receiptApiMock);
        when(receiptApiMock.getReceiptDetail(fiscalCode,brokerId,organizationId,receiptId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        ReceiptDetailExtendedDTO result = receiptClient.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,accessToken);

        Assertions.assertNull(result);
    }

    @Test
    void whenGetPaymentNoticeThenOk(){
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        ByteArrayResource expectedResource = new ByteArrayResource("PDF-DATA".getBytes());
        String expectedFileName = "filename";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(expectedFileName).build());
        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(expectedResource, headers, HttpStatus.OK);

        Mockito.when(citizenApisHolderMock.getReceiptApi(accessToken)).thenReturn(receiptApiMock);
        Mockito.when(receiptApiMock.getReceiptPdfWithHttpInfo(fiscalCode, brokerId, organizationId, receiptId)).thenReturn(responseEntity);

        FileResourceDTO response = receiptClient.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResource,response.getResource());
        Assertions.assertEquals(expectedFileName,response.getFileName());
    }

    @Test
    void givenNoReceiptWhenGetPaymentNoticeThenNull(){
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;

        Mockito.when(citizenApisHolderMock.getReceiptApi(accessToken)).thenReturn(receiptApiMock);
        Mockito.when(receiptApiMock.getReceiptPdfWithHttpInfo(fiscalCode, brokerId, organizationId, receiptId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        FileResourceDTO response = receiptClient.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,accessToken);

        Assertions.assertNull(response);
    }
}