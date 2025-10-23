package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.connector.citizen.DebtPositionService;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.AuthorizationService;
import org.springframework.stereotype.Service;

@Service
public class DebtPositionRetrieverServiceImpl implements DebtPositionRetrieverService {

    private final DebtPositionService debtPositionService;

    public DebtPositionRetrieverServiceImpl(DebtPositionService debtPositionService) {
        this.debtPositionService = debtPositionService;
    }

    @Override
    public FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser) {
        return debtPositionService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, AuthorizationService.getDebtorFiscalCode(fiscalCode,loggedUser));
    }
}
