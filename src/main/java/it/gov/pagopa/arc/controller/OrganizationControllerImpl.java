package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.OrganizationApi;
import it.gov.pagopa.arc.service.organization.OrganizationFacadeService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class OrganizationControllerImpl implements OrganizationApi {

    private final OrganizationFacadeService organizationFacadeService;

    public OrganizationControllerImpl(OrganizationFacadeService organizationFacadeService) {
        this.organizationFacadeService = organizationFacadeService;
    }

    @Override
    public ResponseEntity<List<OrganizationsWithSpontaneousDTO>> getOrganizationsWithSpontaneous(Long brokerId) {
        log.info("getOrganizationsWithSpontaneous was requested with brokerId {}", brokerId);
        return ResponseEntity.ok(organizationFacadeService.getOrganizationsWithSpontaneousDTO(brokerId));
    }

    @Override
    public ResponseEntity<List<OrganizationsWithSpontaneousDTO>> getPublicOrganizationsWithSpontaneous(Long brokerId) {
        log.info("getPublicOrganizationsWithSpontaneous was requested with brokerId {}", brokerId);
        return ResponseEntity.ok(organizationFacadeService.getOrganizationsWithSpontaneousDTO(brokerId));
    }

    @Override
    public ResponseEntity<OrganizationLogoDTO> getPublicOrganizationLogo(Long brokerId, String orgFiscalCode) {
        log.info("getPublicOrganizationLogo was requested with brokerId {} and orgFiscalCode {}", brokerId, orgFiscalCode);
        return ResponseEntity.ofNullable(organizationFacadeService.getOrganizationLogo(brokerId,orgFiscalCode));
    }
}
