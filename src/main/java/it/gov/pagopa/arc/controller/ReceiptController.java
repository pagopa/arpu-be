package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.ReceiptApi;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.service.receipt.ReceiptFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ReceiptController implements ReceiptApi {

    private final ReceiptFacadeService receiptFacadeService;

    public ReceiptController(ReceiptFacadeService receiptFacadeService) {
        this.receiptFacadeService = receiptFacadeService;
    }

    @Override
    public ResponseEntity<PagedDebtorReceiptsDTO> getPagedDebtorReceipts(Long brokerId, String xFiscalCode, String orgName, Pageable pageable) {
        log.info("Requested getPagedDebtorReceipts on brokerId {} and orgName {}", brokerId, orgName);
        return ResponseEntity.ok(receiptFacadeService.getPagedDebtorReceipts(brokerId, xFiscalCode, orgName, pageable, SecurityUtils.getPrincipal()));
    }

    @Override
    public ResponseEntity<ReceiptDetailExtendedDTO> getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String xFiscalCode) {
        log.info("User requested getReceiptDetail having brokerId {} organizationId {} and receiptId {} ", brokerId, organizationId, receiptId);
        return ResponseEntity.ofNullable(receiptFacadeService.getReceiptDetail(brokerId, organizationId, receiptId, xFiscalCode, SecurityUtils.getPrincipal()));
    }

    @Override
    public ResponseEntity<Resource> getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String xFiscalCode) {
        log.info("getReceiptPdf was requested with brokerId {} organizationId {} and receiptId {}", brokerId, organizationId, receiptId);
        return getResourceForGetReceiptPdf(brokerId, organizationId, receiptId, xFiscalCode);
    }

    @Override
    public ResponseEntity<Resource> getPublicReceiptPdf(String xFiscalCode, Long brokerId, Long organizationId, Long receiptId) {
        log.info("getPublicReceiptPdf was requested with brokerId {} organizationId {} and receiptId {}", brokerId, organizationId, receiptId);
        return getResourceForGetReceiptPdf(brokerId, organizationId, receiptId, xFiscalCode);
    }

    private ResponseEntity<Resource> getResourceForGetReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode){
        FileResourceDTO receiptFileResource = receiptFacadeService.getReceiptPdf(brokerId, organizationId, receiptId, debtorFiscalCode, SecurityUtils.getPrincipal());
        if(receiptFileResource == null || receiptFileResource.getResource() == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(receiptFileResource.getFileName())
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(receiptFileResource.getResource());
    }

    @Override
    public ResponseEntity<ReceiptDetailExtendedDTO> getPublicReceiptDetail(String debtorFiscalCode, Long brokerId, Long organizationId, Long receiptId) {
        log.info("User requested getPublicReceiptDetail having brokerId {} organizationId {} and receiptId {} ", brokerId, organizationId, receiptId);
        return ResponseEntity.ofNullable(receiptFacadeService.getReceiptDetail(brokerId, organizationId, receiptId, debtorFiscalCode, SecurityUtils.getPrincipal()));
    }
}
