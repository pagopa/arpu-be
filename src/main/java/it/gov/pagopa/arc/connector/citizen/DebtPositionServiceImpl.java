package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.DebtPositionClient;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
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
    public FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String iuv, String iud) {
        return debtPositionClient.getPaymentNotice(fiscalCode,brokerId, organizationId, installmentId, iuv, iud, authnService.getAccessToken());
    }
}
