package it.gov.pagopa.arc.service.organization;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.CitizenService;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationRetrieveServiceImpl implements OrganizationRetrieveService {

    private final CitizenService citizenService;
    private final AuthnService authnService;

    public OrganizationRetrieveServiceImpl(CitizenService citizenService, AuthnService authnService) {
        this.citizenService = citizenService;
        this.authnService = authnService;
    }

    @Override
    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(Long brokerId) {
        return citizenService.getOrganizationsWithSpontaneousDTO(authnService.getAccessToken(), brokerId);
    }
}
