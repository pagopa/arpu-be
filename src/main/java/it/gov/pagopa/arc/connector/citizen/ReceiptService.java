package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReceiptService {
    PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable);
    ReceiptDetailExtendedDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode);
    FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode);
    List<DebtorReceiptDTO> getDebtorReceipts(String debtorFiscalCode, Long brokerId, Long organizationId, Long debtPositionId, Long paymentOptionId);
}
