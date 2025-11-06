package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;

public interface DebtPositionService {
  FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode);
  DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body);
  DebtPositionDTO getDebtPositionDetail(Long brokerId, Long debtPositionId, String fiscalCode);
  FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String iuv, String iud);
}
