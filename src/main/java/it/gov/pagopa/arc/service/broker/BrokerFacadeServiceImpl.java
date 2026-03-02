package it.gov.pagopa.arc.service.broker;

import it.gov.pagopa.arc.connector.citizen.BrokerService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import org.springframework.stereotype.Service;

@Service
public class BrokerFacadeServiceImpl implements BrokerFacadeService {
    private final BrokerService brokerService;

    public BrokerFacadeServiceImpl(BrokerService brokerService) {
        this.brokerService = brokerService;
    }

    @Override
    public BrokerInfoDTO getBrokerInfo(Long brokerId, String externalId) {
        return brokerService.getBrokerInfo(brokerId, externalId);
    }
}
