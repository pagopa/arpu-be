package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.arc.service.debtpositiontypeorg.DebtPositionTypeOrgFacadeService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DebtPositionTypeOrgController implements DebtPositionTypeOrgApi {

    private final DebtPositionTypeOrgFacadeService debtPositionTypeOrgFacadeService;

    public DebtPositionTypeOrgController(DebtPositionTypeOrgFacadeService debtPositionTypeOrgFacadeService) {
        this.debtPositionTypeOrgFacadeService = debtPositionTypeOrgFacadeService;
    }

    @Override
    public ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId) {
        log.info("getDebtPositionTypeOrgsWithSpontaneous was requested with organizationId {}", organizationId);
        return ResponseEntity.ok(debtPositionTypeOrgFacadeService.getDebtPositionTypeOrgsWithSpontaneous(organizationId));
    }

    @Override
    public ResponseEntity<DebtPositionTypeOrgsWithSpontaneousDetailsDTO> getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId) {
        log.info("getDebtPositionTypeOrgsWithSpontaneousDetail was requested with brokerId {} and organizationId {} and debtPositionTypeOrgId {}", brokerId, organizationId, debtPositionTypeOrgId);
        return ResponseEntity.ok(debtPositionTypeOrgFacadeService.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId));
    }
}
