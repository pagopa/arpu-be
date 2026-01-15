package it.gov.pagopa.arc.service.receipt;

import it.gov.pagopa.arc.connector.citizen.ReceiptService;
import it.gov.pagopa.arc.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.AuthorizationService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorReceiptDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptFacadeServiceImpl implements ReceiptFacadeService{

    private final ReceiptService receiptService;

    public ReceiptFacadeServiceImpl(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Override
    public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, IamUserInfoDTO loggedUser) {
        return receiptService.getPagedDebtorReceipts(brokerId, AuthorizationService.getDebtorFiscalCode(debtorFiscalCode,loggedUser), debtorReceiptsFiltersDTO, pageable);
    }

    @Override
    public ReceiptDetailExtendedDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser) {
        return receiptService.getReceiptDetail(brokerId,organizationId,receiptId,AuthorizationService.getDebtorFiscalCode(debtorFiscalCode,loggedUser));
    }

    @Override
    public FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, IamUserInfoDTO loggedUser) {
        return receiptService.getReceiptPdf(brokerId,organizationId,receiptId,AuthorizationService.getDebtorFiscalCode(debtorFiscalCode,loggedUser));
    }

    @Override
    public List<DebtorReceiptDTO> getDebtorReceipts(String debtorFiscalCode, Long brokerId, Long organizationId, Long debtPositionId, Long paymentOptionId, IamUserInfoDTO loggedUser) {
        return receiptService.getDebtorReceipts(AuthorizationService.getDebtorFiscalCode(debtorFiscalCode,loggedUser),brokerId,organizationId,debtPositionId,paymentOptionId);
    }
}
