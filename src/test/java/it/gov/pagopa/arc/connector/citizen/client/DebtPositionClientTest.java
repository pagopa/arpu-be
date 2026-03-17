package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
import it.gov.pagopa.pu.citizen.dto.generated.*;
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

import java.util.ArrayList;

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
    void givenNoDebtPositionWhenGetUnpaidPaymentNoticeZipThenNull(){
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getUnpaidPaymentNoticeZipWithHttpInfo(brokerId, fiscalCode, debtPositionId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        FileResourceDTO response = debtPositionClient.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, accessToken);

        Assertions.assertNull(response);
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
      String nav = "nav";
      ByteArrayResource expectedResource = new ByteArrayResource("PDF-DATA".getBytes());
      String expectedFileName = "filename";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentDisposition(
          ContentDisposition.attachment().filename(expectedFileName).build());
      ResponseEntity<Resource> responseEntity = new ResponseEntity<>(expectedResource, headers, HttpStatus.OK);

      Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
      Mockito.when(debtPositionApiMock.getPaymentNoticeWithHttpInfo(fiscalCode, brokerId, organizationId, nav)).thenReturn(responseEntity);

      FileResourceDTO response = debtPositionClient.getPaymentNotice(fiscalCode, brokerId, organizationId, nav, accessToken);

      Assertions.assertNotNull(response);
      Assertions.assertEquals(expectedResource,response.getResource());
      Assertions.assertEquals(expectedFileName,response.getFileName());
    }

    @Test
    void givenNotFoundWhenGetPaymentNoticeThenNull(){
      String accessToken = "accessToken";
      String fiscalCode = "fiscalCode";
      Long brokerId = 1L;
      Long organizationId = 2L;
      String nav = "nav";

      Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
      Mockito.when(debtPositionApiMock.getPaymentNoticeWithHttpInfo(fiscalCode, brokerId, organizationId, nav))
              .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

      FileResourceDTO response = debtPositionClient.getPaymentNotice(fiscalCode, brokerId, organizationId, nav, accessToken);

      Assertions.assertNull(response);
    }

    @Test
    void whenGetPagedDebtorDebtPositionThenOk() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        String orgName = "orgName";
        String orgFiscalCode = "orgFiscalCode";
        String fiscalCode = "fiscalCode";
        PageRequest pageRequest = PageRequest.of(1, 10);

        PagedDebtorDebtPositionDTO expectedResult = new PagedDebtorDebtPositionDTO();
        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getPagedUnpaidDebtPositions(fiscalCode, brokerId, orgName, orgFiscalCode, 1,10, new ArrayList<>())).thenReturn(expectedResult);
        //when
        PagedDebtorDebtPositionDTO result = debtPositionClient.getPagedDebtorDebtPosition(fiscalCode, brokerId, orgName, orgFiscalCode, pageRequest, accessToken);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    void whenGetDebtorUnpaidDebtPositionOverviewThenOk() {
        // given
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        Long organizationId = 3L;

        DebtorUnpaidDebtPositionOverviewDTO expectedResult = new DebtorUnpaidDebtPositionOverviewDTO();

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getDebtorUnpaidDebtPositionOverview(brokerId, debtPositionId, fiscalCode, organizationId))
                .thenReturn(expectedResult);

        // when
        DebtorUnpaidDebtPositionOverviewDTO result = debtPositionClient.getDebtorUnpaidDebtPositionOverview(
                brokerId, debtPositionId, fiscalCode, organizationId, accessToken);

        // then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }

    @Test
    void givenNotFoundWhenGetDebtorUnpaidDebtPositionOverviewThenReturnNull() {
        // given
        String accessToken = "accessToken";
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        Long organizationId = 3L;

        Mockito.when(citizenApisHolderMock.getDebtPositionApi(accessToken)).thenReturn(debtPositionApiMock);
        Mockito.when(debtPositionApiMock.getDebtorUnpaidDebtPositionOverview(brokerId, debtPositionId, fiscalCode, organizationId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        // when
        DebtorUnpaidDebtPositionOverviewDTO result = debtPositionClient.getDebtorUnpaidDebtPositionOverview(
                brokerId, debtPositionId, fiscalCode, organizationId, accessToken);

        // then
        assertNull(result);
    }

}