package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.ReceiptApi;
import it.gov.pagopa.arc.service.receipt.ReceiptFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
}
