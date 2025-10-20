package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.citizen.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionTypeOrgServiceImpl implements DebtPositionTypeOrgService{

    private final DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    public DebtPositionTypeOrgServiceImpl(DebtPositionTypeOrgClient debtPositionTypeOrgClient) {
        this.debtPositionTypeOrgClient = debtPositionTypeOrgClient;
    }

    @Override
    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId, String accessToken) {
        return debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken);
    }
}
