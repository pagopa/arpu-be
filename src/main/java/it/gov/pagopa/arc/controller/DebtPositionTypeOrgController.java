package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.arc.service.debtpositiontypeorg.DebtPositionTypeOrgRetrieverService;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DebtPositionTypeOrgController implements DebtPositionTypeOrgApi {

    private final DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService;

    public DebtPositionTypeOrgController(DebtPositionTypeOrgRetrieverService debtPositionTypeOrgRetrieverService) {
        this.debtPositionTypeOrgRetrieverService = debtPositionTypeOrgRetrieverService;
    }

    @Override
    public ResponseEntity<List<DebtPositionTypeOrgsWithSpontaneousDTO>> getDebtPositionTypeOrgsWithSpontaneous(Long organizationId) {
        log.info("getDebtPositionTypeOrgsWithSpontaneous was requested with organizationId {}", organizationId);
        return ResponseEntity.ok(debtPositionTypeOrgRetrieverService.getDebtPositionTypeOrgsWithSpontaneous(organizationId));
    }
}
