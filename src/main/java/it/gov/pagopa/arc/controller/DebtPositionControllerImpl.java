package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.DebtPositionApi;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.service.debtpositions.DebtPositionFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class DebtPositionControllerImpl implements DebtPositionApi {

  private final DebtPositionFacadeService debtPositionFacadeService;

  public DebtPositionControllerImpl(DebtPositionFacadeService debtPositionFacadeService) {
    this.debtPositionFacadeService = debtPositionFacadeService;
  }

    @Override
    public ResponseEntity<Resource> getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode) {
      log.info("getUnpaidPaymentNoticeZip was requested with brokerId {} and debtPositionId {}", brokerId, debtPositionId);
      return getResourceForUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode);
    }

    private ResponseEntity<Resource> getResourceForUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode) {
        FileResourceDTO debtPositionPaymentNoticesZipped = debtPositionFacadeService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode,SecurityUtils.getPrincipal());
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
    log.info("createSpontaneousDebtPosition was requested with brokerId {} and organizationId {}", brokerId, body.getOrganizationId());
    return ResponseEntity.ok(debtPositionFacadeService.createSpontaneousDebtPosition(brokerId, body));
  }

  @Override
  public ResponseEntity<DebtPositionDTO> getDebtPositionDetail(Long brokerId, Long debtPositionId, String xFiscalCode) {
    log.info("getDebtPositionDetail was requested with brokerId {} and debtPositionId {}", brokerId,debtPositionId);
    return ResponseEntity.ok(debtPositionFacadeService.getDebtPositionDetail(brokerId, debtPositionId, xFiscalCode, SecurityUtils.getPrincipal()));
  }

  @Override
  public ResponseEntity<Resource> getPaymentNotice(Long brokerId, Long organizationId, String fiscalCode, Long installmentId, String iuv, String iud) {
      log.info("getPaymentNotice was requested with brokerId {} and organizationId {}", brokerId, organizationId);

      FileResourceDTO paymentNoticeFileResource = debtPositionFacadeService.getPaymentNotice(fiscalCode, brokerId, organizationId, installmentId, iuv, iud,SecurityUtils.getPrincipal());
      if (paymentNoticeFileResource != null && paymentNoticeFileResource.getResource()!=null){
          HttpHeaders headers = new HttpHeaders();
          headers.setContentDisposition(ContentDisposition.attachment()
                  .filename(paymentNoticeFileResource.getFileName())
                  .build());

          return ResponseEntity.ok()
                  .headers(headers)
                  .contentType(MediaType.APPLICATION_PDF)
                  .body(paymentNoticeFileResource.getResource());
      } else {
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
  }

    @Override
    public ResponseEntity<DebtPositionResponseDTO> createPublicSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body) {
        log.info("createPublicSpontaneousDebtPosition was requested with brokerId {} and organizationId {}", brokerId, body.getOrganizationId());
        return ResponseEntity.ok(debtPositionFacadeService.createSpontaneousDebtPosition(brokerId, body));
    }

    @Override
    public ResponseEntity<Resource> getPublicUnpaidPaymentNoticeZip(Long brokerId, String xFiscalCode, Long debtPositionId) {
        log.info("getPublicUnpaidPaymentNoticeZip was requested with brokerId {} and debtPositionId {}", brokerId, debtPositionId);
        return getResourceForUnpaidPaymentNoticeZip(brokerId, debtPositionId, xFiscalCode);
    }
}
