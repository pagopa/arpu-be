package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;

public interface DebtPositionTypeOrgService {
    List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId);
    DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId);
    List<DebtPositionTypeOrgsWithSpontaneousDTO> getMostUsedSpontaneousDebtPositionTypeOrgs(Long brokerId, Long organizationId, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo, Pageable pageable);
}
