package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.connector.citizen.DebtPositionService;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.AuthorizationService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionFacadeServiceImpl implements DebtPositionFacadeService {

    private final DebtPositionService debtPositionService;

    public DebtPositionFacadeServiceImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser) {
        return debtPositionService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, AuthorizationService.getDebtorFiscalCode(fiscalCode,loggedUser));
    }

    @Override
    public DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body) {
        return debtPositionService.createSpontaneousDebtPosition(brokerId,body);
    }
}
