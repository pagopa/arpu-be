package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.DebtPositionClient;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionServiceImpl implements DebtPositionService{
    private final AuthnService authnService;
    private final DebtPositionClient debtPositionClient;

    public DebtPositionServiceImpl(AuthnService authnService, DebtPositionClient debtPositionClient) {
        this.authnService = authnService;
        this.debtPositionClient = debtPositionClient;
    }

    @Override
    public FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode) {
        return debtPositionClient.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, authnService.getAccessToken());
    }

    @Override
    public DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body) {
        return debtPositionClient.createSpontaneousDebtPosition(brokerId, body, authnService.getAccessToken());
    }

    @Override
    public DebtPositionDTO getDebtPositionDetail(Long brokerId, Long debtPositionId, String fiscalCode) {
        return debtPositionClient.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, authnService.getAccessToken());
    }

    @Override
    public FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String nav, String iud) {
        return debtPositionClient.getPaymentNotice(fiscalCode,brokerId, organizationId, installmentId, nav, iud, authnService.getAccessToken());
    }

    @Override
    public PagedDebtorDebtPositionDTO getPagedDebtorDebtPosition(String fiscalCode, Long brokerId, String orgName, String orgFiscalCode, Pageable pageable) {
        return debtPositionClient.getPagedDebtorDebtPosition(fiscalCode, brokerId, orgName, orgFiscalCode, pageable, authnService.getAccessToken());
    }

    @Override
    public DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String xFiscalCode, Long organizationId) {
        return debtPositionClient.getDebtorUnpaidDebtPositionOverview(brokerId, debtPositionId, xFiscalCode, organizationId, authnService.getAccessToken());
    }
}
