package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.OrganizationApi;
import it.gov.pagopa.arc.service.organization.OrganizationRetrieveService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class OrganizationControllerImpl implements OrganizationApi {

    private final OrganizationRetrieveService organizationRetrieveService;

    public OrganizationControllerImpl(OrganizationRetrieveService organizationRetrieveService) {
        this.organizationRetrieveService = organizationRetrieveService;
    }

    @Override
    public ResponseEntity<List<OrganizationsWithSpontaneousDTO>> getOrganizationsWithSpontaneous(Long brokerId) {
        log.info("getOrganizationsWithSpontaneous was requested with brokerId {}", brokerId);
        return ResponseEntity.ok(organizationRetrieveService.getOrganizationsWithSpontaneousDTO(brokerId));
    }
}
