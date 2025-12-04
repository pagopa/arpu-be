package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.connector.citizen.DebtPositionService;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.exception.custom.ResourceNotFoundException;
import it.gov.pagopa.arc.service.AuthorizationService;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import org.springframework.data.domain.Pageable;
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

    @Override
    public DebtPositionDTO getDebtPositionDetail(Long brokerId, Long debtPositionId, String fiscalCode, IamUserInfoDTO loggedUser) {
        DebtPositionDTO debtPositionDetail = debtPositionService.getDebtPositionDetail(brokerId, debtPositionId, AuthorizationService.getDebtorFiscalCode(fiscalCode,loggedUser));
        if (debtPositionDetail == null) {
            throw new ResourceNotFoundException("DebtPosition with debtPositionId %s not found".formatted(debtPositionId));
        }

        return debtPositionDetail;
    }

    @Override
    public FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, Long installmentId, String iuv, String iud, IamUserInfoDTO loggedUser) {
        return debtPositionService.getPaymentNotice(AuthorizationService.getDebtorFiscalCode(fiscalCode,loggedUser), brokerId, organizationId,installmentId, iuv, iud);
    }

    @Override
    public PagedDebtorDebtPositionDTO getPagedUnpaidDebtPositions(Long brokerId, String xFiscalCode, String orgName, String orgFiscalCode, Pageable pageable, IamUserInfoDTO loggedUser) {
        return debtPositionService.getPagedDebtorDebtPosition(AuthorizationService.getDebtorFiscalCode(xFiscalCode, loggedUser), brokerId, orgName, orgFiscalCode, pageable);
    }

    @Override
    public DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String xFiscalCode, Long organizationId, IamUserInfoDTO loggedUser) {
        return debtPositionService.getDebtorUnpaidDebtPositionOverview(brokerId, debtPositionId, AuthorizationService.getDebtorFiscalCode(xFiscalCode,loggedUser), organizationId);
    }
}
