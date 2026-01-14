package it.gov.pagopa.arc.service.debtpositiontypeorg;

import it.gov.pagopa.arc.connector.citizen.DebtPositionTypeOrgService;
import it.gov.pagopa.arc.exception.custom.ResourceNotFoundException;
import it.gov.pagopa.arc.utils.Constants;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DebtPositionTypeOrgFacadeServiceImpl implements DebtPositionTypeOrgFacadeService {

    private final DebtPositionTypeOrgService debtPositionTypeOrgRetrieverService;

    public DebtPositionTypeOrgFacadeServiceImpl(DebtPositionTypeOrgService debtPositionTypeOrgRetrieverService) {
        this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
    }

    @Override
    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId) {
        return debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId);
    }

    @Override
    public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId) {
        DebtPositionTypeOrgsWithSpontaneousDetailsDTO debtPositionTypeOrgsWithSpontaneousDetail = debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
        if (debtPositionTypeOrgsWithSpontaneousDetail == null){
            throw new ResourceNotFoundException("DebtPositionTypeOrgsWithSpontaneousDetails with deptPositionTypeOrgId %d brokerId %d and organizationId %d not found".formatted(debtPositionTypeOrgId, brokerId, organizationId));
        }

        return debtPositionTypeOrgsWithSpontaneousDetail;
    }

    @Override
    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getMostUsedSpontaneousDebtPositionTypeOrgsForCurrentYear(Long brokerId, Long organizationId) {
        Pageable pageable = Pageable.ofSize(10);
        OffsetDateTime creationDateTo = OffsetDateTime.now(Constants.ZONEID);
        OffsetDateTime creationDateFrom = creationDateTo.minusYears(1);
        return debtPositionTypeOrgRetrieverService.getMostUsedSpontaneousDebtPositionTypeOrgs(brokerId, organizationId, creationDateFrom, creationDateTo, pageable);
    }
}
