package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionApi;
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
}