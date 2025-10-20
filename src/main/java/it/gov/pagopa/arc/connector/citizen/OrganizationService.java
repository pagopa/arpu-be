package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;

import java.util.List;

public interface OrganizationService {
    List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(String accessToken, Long brokerId);

}
