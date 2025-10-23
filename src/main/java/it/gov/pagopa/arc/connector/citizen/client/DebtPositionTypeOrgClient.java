package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionTypeOrgClient {

    private final CitizenApisHolder citizenApisHolder;

    public DebtPositionTypeOrgClient(CitizenApisHolder citizenApisHolder) {
        this.citizenApisHolder = citizenApisHolder;
    }

    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId, String accessToken){
        return citizenApisHolder.getDebtPositionTypeOrgApi(accessToken)
                .getDebtPositionTypeOrgsWithSpontaneous(organizationId);
    }

    public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId, String accessToken){
        return citizenApisHolder.getDebtPositionTypeOrgApi(accessToken).getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
    }
}
