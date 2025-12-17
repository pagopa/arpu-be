package it.gov.pagopa.arc.service.receipt;

import it.gov.pagopa.arc.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import org.springframework.data.domain.Pageable;

public interface ReceiptFacadeService {
    PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, IamUserInfoDTO loggedUser);
    ReceiptDetailExtendedDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser);
    FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser);
}
