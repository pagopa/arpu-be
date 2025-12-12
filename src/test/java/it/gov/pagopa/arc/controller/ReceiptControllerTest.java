package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.receipt.ReceiptFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtilsTest;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void whenGetReceiptDetailThenOk() {
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";
        ReceiptDetailDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailDTO.class);

        Mockito.when(receiptFacadeServiceMock.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,loggedUser)).thenReturn(expectedResult);
        //when
        ResponseEntity<ReceiptDetailDTO> result = receiptController.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }

    @Test
    void givenNoReceiptWhenGetReceiptDetailThenNotFound() {
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";

        Mockito.when(receiptFacadeServiceMock.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,loggedUser)).thenReturn(null);

        ResponseEntity<ReceiptDetailDTO> result = receiptController.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void whenGetReceiptPdfThenOk() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;

        FileResourceDTO resource = podamFactory.manufacturePojo(FileResourceDTO.class);
        resource.setResource(new ByteArrayResource("PDF-DATA".getBytes()));

        Mockito.when(receiptFacadeServiceMock.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,loggedUser))
                .thenReturn(resource);

        ResponseEntity<Resource> response = receiptController.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource.getResource(), response.getBody());
        assertEquals(resource.getFileName(), response.getHeaders().getContentDisposition().getFilename());
    }

    @Test
    void givenNullResourceWhenGetReceiptPdfThenNoContent() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;

        Mockito.when(receiptFacadeServiceMock.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,loggedUser))
                .thenReturn(null);

        ResponseEntity<Resource> response = receiptController.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void whenGetPublicReceiptPdfThenOk() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;

        FileResourceDTO resource = podamFactory.manufacturePojo(FileResourceDTO.class);
        resource.setResource(new ByteArrayResource("PDF-DATA".getBytes()));

        Mockito.when(receiptFacadeServiceMock.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,loggedUser))
                .thenReturn(resource);

        ResponseEntity<Resource> response = receiptController.getPublicReceiptPdf(fiscalCode,brokerId,organizationId,receiptId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource.getResource(), response.getBody());
        assertEquals(resource.getFileName(), response.getHeaders().getContentDisposition().getFilename());
    }

    @Test
    void givenNullResourceWhenGetPublicReceiptPdfThenNoContent() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;

        Mockito.when(receiptFacadeServiceMock.getReceiptPdf(brokerId,organizationId,receiptId,fiscalCode,loggedUser))
                .thenReturn(null);

        ResponseEntity<Resource> response = receiptController.getPublicReceiptPdf(fiscalCode,brokerId,organizationId,receiptId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void whenGetPublicReceiptDetailThenOk() {
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long receiptId = 3L;
        String fiscalCode = "fiscalCode";
        ReceiptDetailDTO expectedResult = podamFactory.manufacturePojo(ReceiptDetailDTO.class);

        Mockito.when(receiptFacadeServiceMock.getReceiptDetail(brokerId,organizationId,receiptId,fiscalCode,loggedUser)).thenReturn(expectedResult);
        //when
        ResponseEntity<ReceiptDetailDTO> result = receiptController.getPublicReceiptDetail(fiscalCode, brokerId, organizationId, receiptId);
        //then
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResult, result.getBody());
    }
}