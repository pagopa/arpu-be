package it.gov.pagopa.arc.service.debtpositiontypeorg;

import it.gov.pagopa.arc.connector.citizen.DebtPositionTypeOrgService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DebtPositionTypeOrgRetrieverServiceImpl implements DebtPositionTypeOrgRetrieverService{

    private final DebtPositionTypeOrgService debtPositionTypeOrgRetrieverService;

    public DebtPositionTypeOrgRetrieverServiceImpl(DebtPositionTypeOrgService debtPositionTypeOrgRetrieverService) {
        this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
    }

    @Override
    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId) {
        return debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(organizationId);
    }

    @Override
    public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId) {
        return debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
    }
}
