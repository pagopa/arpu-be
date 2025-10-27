package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.DebtPositionApi;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.service.debtpositions.DebtPositionRetrieverService;
import it.gov.pagopa.arc.utils.SecurityUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DebtPositionControllerImpl implements DebtPositionApi {

  private final DebtPositionRetrieverService debtPositionRetrieverService;

  public DebtPositionControllerImpl(DebtPositionRetrieverService debtPositionRetrieverService) {
    this.debtPositionRetrieverService = debtPositionRetrieverService;
  }

  @Override
  public ResponseEntity<Resource> getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode) {
    log.info("getUnpaidPaymentNoticeZip was requested with brokerId {} and debtPositionId {}", brokerId, debtPositionId);

    FileResourceDTO debtPositionPaymentNoticesZipped = debtPositionRetrieverService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId,fiscalCode,SecurityUtils.getPrincipal());
    if (debtPositionPaymentNoticesZipped != null && debtPositionPaymentNoticesZipped.getResource()!=null){
      HttpHeaders headers = new HttpHeaders();
      headers.setContentDisposition(ContentDisposition.attachment()
          .filename(debtPositionPaymentNoticesZipped.getFileName())
          .build());

      return ResponseEntity.ok()
          .headers(headers)
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .body(debtPositionPaymentNoticesZipped.getResource());
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }

  @Override
  public ResponseEntity<DebtPositionResponseDTO> createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body) {
    log.info("createSpontaneousDebtPosition was requested with brokerId {}", brokerId);
    return ResponseEntity.ok(debtPositionRetrieverService.createSpontaneousDebtPosition(brokerId, body));
  }
}
