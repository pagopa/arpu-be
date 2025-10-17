package it.gov.pagopa.arc.connector.citizen.client;


import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.pu.citizen.dto.generated.OrganizationsWithSpontaneousDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OrganizationClient {

    private final CitizenApisHolder apisHolder;

    public OrganizationClient(CitizenApisHolder apisHolder) {
        this.apisHolder = apisHolder;
    }

    public List<OrganizationsWithSpontaneousDTO> getOrganizationsWithSpontaneousDTO(String accessToken, Long brokerId){
        return apisHolder.getOrganizationApi(accessToken).getOrganizationsWithSpontaneous(brokerId);
    }
}
