package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.DebtPositionClient;
import it.gov.pagopa.arc.dto.FileResourceDTO;
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
}
