package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;

public interface DebtPositionRetrieverService {
  FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser);
  DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body);
}
