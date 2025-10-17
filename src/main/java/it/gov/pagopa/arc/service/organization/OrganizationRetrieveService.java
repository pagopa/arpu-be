package it.gov.pagopa.arc.service.organization;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;

import java.util.List;

public interface OrganizationRetrieveService {
    List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(Long brokerId);
}
