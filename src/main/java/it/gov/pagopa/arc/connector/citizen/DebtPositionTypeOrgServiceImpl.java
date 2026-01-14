package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService{

    private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;
    private final AuthnService authnService;

    public DebtPositionTypeOrgServiceImpl(DebtPositionTypeOrgClient debtPositionTypeOrgClient, AuthnService authnService) {
        this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
        this.authnService = authnService;
    }

    @Override
    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId) {
        return debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId, authnService.getAccessToken());
    }

    @Override
    public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId) {
        return debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId, authnService.getAccessToken());
    }

    @Override
    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getMostUsedSpontaneousDebtPositionTypeOrgs(Long brokerId, Long organizationId, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo, Pageable pageable) {
        return debtPositionTypeOrgClient.getMostUsedSpontaneousDebtPositionTypeOrgs(brokerId, organizationId,creationDateFrom, creationDateTo, pageable, authnService.getAccessToken());
    }
}
