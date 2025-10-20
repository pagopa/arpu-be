package it.gov.pagopa.arc.service.organization;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationRetrieveServiceImpl implements OrganizationRetrieveService {

    private final OrganizationService organizationService;
    private final AuthnService authnService;

    public OrganizationRetrieveServiceImpl(OrganizationService organizationService, AuthnService authnService) {
        this.organizationService = organizationService;
        this.authnService = authnService;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(Long brokerId) {
        return organizationService.getOrganizationsWithSpontaneousDTO(authnService.getAccessToken(), brokerId);
    }
}
