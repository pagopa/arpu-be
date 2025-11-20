package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailDTO;
import org.springframework.data.domain.Pageable;

public interface ReceiptService {
    PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, String orgName, Pageable pageable);
    ReceiptDetailDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode);
    FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode);
}
