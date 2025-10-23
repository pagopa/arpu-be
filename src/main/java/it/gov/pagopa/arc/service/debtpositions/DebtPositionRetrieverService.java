package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;

public interface DebtPositionRetrieverService {
  FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser);
}
