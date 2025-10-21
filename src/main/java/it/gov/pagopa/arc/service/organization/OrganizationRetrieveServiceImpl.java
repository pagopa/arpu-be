package it.gov.pagopa.arc.service.organization;

import it.gov.pagopa.arc.connector.citizen.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationRetrieveServiceImpl implements OrganizationRetrieveService {

    private final OrganizationService organizationService;

    public OrganizationRetrieveServiceImpl(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(Long brokerId) {
        return organizationService.getOrganizationsWithSpontaneousDTO(brokerId);
    }
}
