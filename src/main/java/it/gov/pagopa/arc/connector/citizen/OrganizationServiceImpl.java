package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.OrganizationClient;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationClient organizationClient;
    private final AuthnService authnService;


    public OrganizationServiceImpl(OrganizationClient organizationClient, AuthnService authnService) {
        this.organizationClient = organizationClient;
        this.authnService = authnService;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(Long brokerId) {
        return organizationClient.getOrganizationsWithSpontaneousDTO(authnService.getAccessToken(), brokerId);
    }
}
