package it.gov.pagopa.arc.service.receipt;

import it.gov.pagopa.arc.connector.citizen.ReceiptService;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.AuthorizationService;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReceiptFacadeServiceImpl implements ReceiptFacadeService{

    private final ReceiptService receiptService;

    public ReceiptFacadeServiceImpl(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Override
    public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, String orgName, Pageable pageable, IamUserInfoDTO loggedUser) {
        return receiptService.getPagedDebtorReceipts(brokerId, AuthorizationService.getDebtorFiscalCode(debtorFiscalCode,loggedUser), orgName, pageable);
    }

    @Override
    public ReceiptDetailDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser) {
        return receiptService.getReceiptDetail(brokerId,organizationId,receiptId,AuthorizationService.getDebtorFiscalCode(debtorFiscalCode,loggedUser));
    }
}
