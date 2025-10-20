package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.citizen.client.OrganizationClient;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationClient organizationClient;

    public OrganizationServiceImpl(OrganizationClient organizationClient) {
        this.organizationClient = organizationClient;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(String accessToken, Long brokerId) {
        return organizationClient.getOrganizationsWithSpontaneousDTO(accessToken, brokerId);
    }
}
