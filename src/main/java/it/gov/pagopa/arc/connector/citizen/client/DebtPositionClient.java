package it.gov.pagopa.arc.connector.citizen.client;


import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DebtPositionClient {

    private final CitizenApisHolder apisHolder;

    public DebtPositionClient(CitizenApisHolder apisHolder) {
        this.apisHolder = apisHolder;
    }

  public FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, String accessToken) {
    ResponseEntity<Resource> resourceResponseEntity = apisHolder.getDebtPositionApi(accessToken)
        .getUnpaidPaymentNoticeZipWithHttpInfo(brokerId,fiscalCode,debtPositionId);
    return FileResourceDTO.builder()
        .resource(resourceResponseEntity.getBody())
        .fileName(resourceResponseEntity.getHeaders().getContentDisposition().getFilename())
        .build();
  }
}
