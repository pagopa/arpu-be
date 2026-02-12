package it.gov.pagopa.arc.service.installment;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentStatus;

import java.util.List;

public interface InstallmentFacadeService {
    List<InstallmentDebtorExtendedDTO> getPublicInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode, List<InstallmentStatus> statuses);
    List<DebtorUnpaidDebtPositionInstallmentsDTO> getDebtorUnpaidDebtPositionInstallments(Long brokerId, Long debtPositionId, Long paymentOptionId, String xFiscalCode, Long organizationId, IamUserInfoDTO loggedUser);
}
