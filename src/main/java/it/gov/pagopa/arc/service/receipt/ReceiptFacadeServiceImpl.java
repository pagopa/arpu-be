package it.gov.pagopa.arc.service.receipt;

import it.gov.pagopa.arc.connector.citizen.ReceiptService;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReceiptFacadeServiceImpl implements ReceiptFacadeService{

    private final ReceiptService receiptService;

    public ReceiptFacadeServiceImpl(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Override
    public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, String orgName, Pageable pageable) {
        return receiptService.getPagedDebtorReceipts(brokerId, debtorFiscalCode, orgName, pageable);
    }
}
