package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;

import java.util.List;

public interface InstallmentService {
    List<InstallmentDebtorExtendedDTO> getInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode);
    List<DebtorUnpaidDebtPositionInstallmentsDTO> getDebtorUnpaidDebtPositionInstallments(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId);
}
