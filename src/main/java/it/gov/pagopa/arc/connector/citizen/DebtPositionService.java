package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import org.springframework.data.domain.Pageable;

public interface DebtPositionService {
  FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode);
  DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body);
  DebtPositionDTO getDebtPositionDetail(Long brokerId, Long debtPositionId, String fiscalCode);
  FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String iuv, String iud);
  PagedDebtorDebtPositionDTO getPagedDebtorDebtPosition(String fiscalCode, Long brokerId, String orgName, String orgFiscalCode, Pageable pageable);
  DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String xFiscalCode, Long organizationId);
}
