package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.dto.FileResourceDTO;

public interface DebtPositionService {
  FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode);
}
