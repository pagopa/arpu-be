package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import org.springframework.data.domain.Pageable;

public interface DebtPositionFacadeService {
  FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser);
  DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body);
  DebtPositionDTO getDebtPositionDetail(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser);
  FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String nav, String iud, IamUserInfoDTO loggedUser);
  PagedDebtorDebtPositionDTO getPagedUnpaidDebtPositions(Long brokerId, String xFiscalCode, String orgName, String orgFiscalCode, Pageable pageable, IamUserInfoDTO loggedUser);
  DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String xFiscalCode, Long organizationId, IamUserInfoDTO loggedUser);
}
