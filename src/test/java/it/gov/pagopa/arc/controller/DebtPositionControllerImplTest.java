package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.DebtPositionApi;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.debtpositions.DebtPositionFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtilsTest;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionControllerImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private DebtPositionFacadeService debtPositionFacadeServiceMock;
    private DebtPositionApi debtPositionController;
    private final IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

    @BeforeEach
    void setUp() {
        SecurityUtilsTest.configureSecurityContext(loggedUser);
        debtPositionController = new DebtPositionControllerImpl(debtPositionFacadeServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionFacadeServiceMock
        );
        SecurityUtilsTest.clearSecurityContext();
    }

    @AfterEach
    void clearContext() {
        SecurityUtilsTest.clearSecurityContext();
    }

    @Test
    void whenGetUnpaidPaymentNoticeZipThenOk() {
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        String fiscalCode = "fiscalCode";

        FileResourceDTO resource = podamFactory.manufacturePojo(FileResourceDTO.class);
        resource.setResource(new ByteArrayResource("PDF-DATA".getBytes()));

        Mockito.when(debtPositionFacadeServiceMock.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, loggedUser))
                .thenReturn(resource);

        ResponseEntity<Resource> response = debtPositionController.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource.getResource(), response.getBody());
        assertEquals(resource.getFileName(), response.getHeaders().getContentDisposition().getFilename());
    }

    @Test
    void givenNullResourceWhenGetUnpaidPaymentNoticeZipThenNoContent() {
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        String fiscalCode = "fiscalCode";

        Mockito.when(debtPositionFacadeServiceMock.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, loggedUser))
                .thenReturn(null);

        ResponseEntity<Resource> response = debtPositionController.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void givenBrokerIdAndDebtPositionRequestDTOWhenCreateSpontaneousDebtPositionThenReturnDebtPositionResponseDTO() {
        //given
        Long brokerId = 1L;
        DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
        DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

        Mockito.when(debtPositionFacadeServiceMock.createSpontaneousDebtPosition(brokerId,requestDTO)).thenReturn(expectedResult);
        //when
        ResponseEntity<DebtPositionResponseDTO> response = debtPositionController.createSpontaneousDebtPosition(brokerId, requestDTO);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResult, response.getBody());
    }

    @Test
    void givenBrokerIdAndDebtPositionIdAndFiscalCodeWhenGetDebtPositionDetailThenReturnDebtPositionDTO() {
        //given
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;

        DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

        Mockito.when(debtPositionFacadeServiceMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, loggedUser)).thenReturn(expectedResult);
        //when
        ResponseEntity<DebtPositionDTO> response = debtPositionController.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResult, response.getBody());
    }

    @Test
    void whenGetPaymentNoticeThenOk() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long installmentId = 3L;
        String iuv = "iuv";
        String iud = "iud";

        FileResourceDTO resource = podamFactory.manufacturePojo(FileResourceDTO.class);
        resource.setResource(new ByteArrayResource("PDF-DATA".getBytes()));

        Mockito.when(debtPositionFacadeServiceMock.getPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud,loggedUser))
                .thenReturn(resource);

        ResponseEntity<Resource> response = debtPositionController.getPaymentNotice(brokerId,organizationId,fiscalCode,installmentId,iuv,iud);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource.getResource(), response.getBody());
        assertEquals(resource.getFileName(), response.getHeaders().getContentDisposition().getFilename());
    }

    @Test
    void givenNullResourceWhenGetPaymentNoticeThenNoContent() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long installmentId = 3L;
        String iuv = "iuv";
        String iud = "iud";

        Mockito.when(debtPositionFacadeServiceMock.getPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud, loggedUser))
                .thenReturn(null);

        ResponseEntity<Resource> response = debtPositionController.getPaymentNotice(brokerId,organizationId,fiscalCode,installmentId,iuv,iud);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void whenGetPublicPaymentNoticeThenOk() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long installmentId = 3L;
        String iuv = "iuv";
        String iud = "iud";

        FileResourceDTO resource = podamFactory.manufacturePojo(FileResourceDTO.class);
        resource.setResource(new ByteArrayResource("PDF-DATA".getBytes()));

        Mockito.when(debtPositionFacadeServiceMock.getPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud,loggedUser))
                .thenReturn(resource);

        ResponseEntity<Resource> response = debtPositionController.getPublicPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource.getResource(), response.getBody());
        assertEquals(resource.getFileName(), response.getHeaders().getContentDisposition().getFilename());
    }

    @Test
    void givenNullResourceWhenGetPublicPaymentNoticeThenNoContent() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long installmentId = 3L;
        String iuv = "iuv";
        String iud = "iud";

        Mockito.when(debtPositionFacadeServiceMock.getPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud, loggedUser))
                .thenReturn(null);

        ResponseEntity<Resource> response = debtPositionController.getPublicPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void givenBrokerIdAndDebtPositionRequestDTOWhenPublicCreateSpontaneousDebtPositionThenReturnDebtPositionResponseDTO() {
        //given
        Long brokerId = 1L;
        DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
        DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

        Mockito.when(debtPositionFacadeServiceMock.createSpontaneousDebtPosition(brokerId,requestDTO)).thenReturn(expectedResult);
        //when
        ResponseEntity<DebtPositionResponseDTO> response = debtPositionController.createPublicSpontaneousDebtPosition(brokerId, requestDTO);
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResult, response.getBody());
    }

    @Test
    void whenGetPublicUnpaidPaymentNoticeZipThenOk() {
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        String fiscalCode = "fiscalCode";

        FileResourceDTO resource = podamFactory.manufacturePojo(FileResourceDTO.class);
        resource.setResource(new ByteArrayResource("PDF-DATA".getBytes()));

        Mockito.when(debtPositionFacadeServiceMock.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, loggedUser))
                .thenReturn(resource);

        ResponseEntity<Resource> response = debtPositionController.getPublicUnpaidPaymentNoticeZip(brokerId, fiscalCode, debtPositionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(resource.getResource(), response.getBody());
        assertEquals(resource.getFileName(), response.getHeaders().getContentDisposition().getFilename());
    }
}