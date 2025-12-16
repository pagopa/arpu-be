package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.ReceiptClient;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReceiptServiceImpl implements ReceiptService{

    private final ReceiptClient receiptClient;
    private final AuthnService authnService;

    public ReceiptServiceImpl(ReceiptClient receiptClient, AuthnService authnService) {
        this.receiptClient = receiptClient;
        this.authnService = authnService;
    }

    @Override
    public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, String orgName, Pageable pageable) {
        return receiptClient.getPagedDebtorReceipts(brokerId, debtorFiscalCode, orgName, pageable, authnService.getAccessToken());
    }

    @Override
    public ReceiptDetailExtendedDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode) {
        return receiptClient.getReceiptDetail(brokerId, organizationId, receiptId, debtorFiscalCode, authnService.getAccessToken());
    }

    @Override
    public FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode) {
        return receiptClient.getReceiptPdf(brokerId,organizationId,receiptId,debtorFiscalCode,authnService.getAccessToken());
    }
}
