package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.citizen.client.OrganizationClient;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CitizenServiceImpl implements CitizenService{

    private final OrganizationClient organizationClient;

    public CitizenServiceImpl(OrganizationClient organizationClient) {
        this.organizationClient = organizationClient;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(String accessToken, Long brokerId) {
        return organizationClient.getOrganizationsWithSpontaneousDTO(accessToken, brokerId);
    }
}
