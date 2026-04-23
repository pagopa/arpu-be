package it.gov.pagopa.arc.service.organization;

import it.gov.pagopa.arc.connector.citizen.OrganizationService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationLogoDTO;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationFacadeServiceImpl implements OrganizationFacadeService {

    private final OrganizationService organizationService;

    public OrganizationFacadeServiceImpl(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(Long brokerId) {
        return organizationService.getOrganizationsWithSpontaneousDTO(brokerId);
    }

    @Override
    public OrganizationLogoDTO getOrganizationLogo(Long brokerId, String orgFiscalCode) {
        return organizationService.getOrganizationLogo(brokerId,orgFiscalCode);
    }
}
