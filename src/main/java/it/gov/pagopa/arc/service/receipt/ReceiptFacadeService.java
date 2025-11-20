package it.gov.pagopa.arc.service.receipt;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailDTO;
import org.springframework.data.domain.Pageable;

public interface ReceiptFacadeService {
    PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, String orgName, Pageable pageable, IamUserInfoDTO loggedUser);
    ReceiptDetailDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser);
    FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser);
}
