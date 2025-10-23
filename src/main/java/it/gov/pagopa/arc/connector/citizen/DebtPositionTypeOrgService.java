package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;

import java.util.List;

public interface DebtPositionTypeOrgService {
    List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId);
    DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId);
}
