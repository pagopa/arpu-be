package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;

public interface BrokerService {
    BrokerInfoDTO getBrokerInfo(Long brokerId, String externalId);
}
