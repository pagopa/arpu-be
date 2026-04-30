package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.InstallmentApi;
import it.gov.pagopa.arc.service.installment.InstallmentFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtorUnpaidDebtPositionInstallmentsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class InstallmentControllerImpl implements InstallmentApi {

    private final InstallmentFacadeService installmentFacadeService;

    public InstallmentControllerImpl(InstallmentFacadeService installmentFacadeService) {
        this.installmentFacadeService = installmentFacadeService;
    }

    @Override
    public ResponseEntity<List<InstallmentDebtorExtendedDTO>> getPublicInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode, List<InstallmentStatus> statuses) {
        log.info("Requested getPublicInstallmentsByIuvOrNav on brokerId {} iuvOrNav {}", brokerId, iuvOrNav);
        return ResponseEntity.ok(installmentFacadeService.getPublicInstallmentsByIuvOrNav(brokerId, iuvOrNav, debtorFiscalCode, orgFiscalCode, statuses));
    }


    @Override
    public ResponseEntity<List<DebtorUnpaidDebtPositionInstallmentsDTO>> getDebtorUnpaidDebtPositionInstallments(Long brokerId, Long debtPositionId, Long paymentOptionId, Long organizationId, String xFiscalCode) {
        log.info("Requested getDebtorUnpaidDebtPositionInstallments on brokerId {} debtPositionId {} paymentOptionId {} and organizationId {}", brokerId, debtPositionId, paymentOptionId, organizationId);
        return ResponseEntity.ok(installmentFacadeService.getDebtorUnpaidDebtPositionInstallments(brokerId, debtPositionId, paymentOptionId, xFiscalCode, organizationId, SecurityUtils.getPrincipal()));
    }
}
