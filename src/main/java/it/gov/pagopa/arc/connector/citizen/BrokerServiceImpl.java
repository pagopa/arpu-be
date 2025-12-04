package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.BrokerClient;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import org.springframework.stereotype.Service;

@Service
public class BrokerServiceImpl implements BrokerService{
    private final BrokerClient brokerClient;
    private final AuthnService authnService;

    public BrokerServiceImpl(BrokerClient brokerClient, AuthnService authnService) {
        this.brokerClient = brokerClient;
        this.authnService = authnService;
    }

    @Override
    public BrokerInfoDTO getBrokerInfo(Long brokerId) {
        return brokerClient.getBrokerInfo(brokerId, authnService.getAccessToken());
    }
}
