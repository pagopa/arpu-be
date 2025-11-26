package it.gov.pagopa.arc.service.broker;

import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;

public interface BrokerFacadeService {
    BrokerInfoDTO getBrokerInfo(Long brokerId);
}
