package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionClientTest {
    @Mock
    private CitizenApisHolder citizenApisHolderMock;
    @Mock
    private DebtPositionApi debtPositionApiMock;
    private DebtPositionClient debtPositionClient;

    @BeforeEach
    void setUp() {
        debtPositionClient = new DebtPositionClient(citizenApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                citizenApisHolderMock,
            debtPositionApiMock
        );
    }

    @Test
    void whenGetUnpaidPaymentNoticeZipThenOk(){
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        ByteArrayResource expectedResource = new ByteArrayResource("PDF-DATA".getBytes());
        String expectedFileName = "filename";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(expectedFileName).build());
        ResponseEntity<Resource> responseEntity = new ResponseEntity<>(expectedResource, headers, HttpStatus.OK);

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getUnpaidPaymentNoticeZipWithHttpInfo(brokerId, fiscalCode, debtPositionId)).thenReturn(responseEntity);

        FileResourceDTO response = debtPositionClient.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResource,response.getResource());
        Assertions.assertEquals(expectedFileName,response.getFileName());
    }

    @Test
    void whenCreateSpontaneousDebtPositionThenOk() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;

        DebtPositionRequestDTO requestDTO = new DebtPositionRequestDTO();
        DebtPositionResponseDTO expectedResult = new DebtPositionResponseDTO();

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.createSpontaneousDebtPosition(brokerId, requestDTO)).thenReturn(expectedResult);
        //when
        DebtPositionResponseDTO result = debtPositionClient.createSpontaneousDebtPosition(brokerId, requestDTO, accessToken);
        //then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }

    @Test
    void whenGetDebtPositionDetailThenOk() {
        //given
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;

        DebtPositionDTO expectedResult = new DebtPositionDTO();

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode)).thenReturn(expectedResult);
        //when
        DebtPositionDTO result = debtPositionClient.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, accessToken);
        //then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundExceptionWhenGetDebtPositionDetailThenReturnNull() {
        //given
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));
        //when
        DebtPositionDTO result = debtPositionClient.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, accessToken);
        //then
        assertNull(result);
    }

    @Test
    void whenGetPaymentNoticeThenOk(){
      String accessToken = "accessToken";
      String fiscalCode = "fiscalCode";
      Long brokerId = 1L;
      Long organizationId = 2L;
      Long installmentId = 3L;
      String iuv = "iuv";
      String iud = "iud";
      ByteArrayResource expectedResource = new ByteArrayResource("PDF-DATA".getBytes());
      String expectedFileName = "filename";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentDisposition(
          ContentDisposition.attachment().filename(expectedFileName).build());
      ResponseEntity<Resource> responseEntity = new ResponseEntity<>(expectedResource, headers, HttpStatus.OK);

      Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
      Mockito.when(debtPositionApiMock.getPaymentNoticeWithHttpInfo(fiscalCode, brokerId, organizationId, installmentId, iuv, iud)).thenReturn(responseEntity);

      FileResourceDTO response = debtPositionClient.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, iuv, iud, accessToken);

      Assertions.assertNotNull(response);
      Assertions.assertEquals(expectedResource,response.getResource());
      Assertions.assertEquals(expectedFileName,response.getFileName());
    }
}