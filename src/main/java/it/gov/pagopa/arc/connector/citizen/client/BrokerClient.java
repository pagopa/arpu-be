package it.gov.pagopa.arc.connector.citizen.client;


import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class BrokerClient {
    private final CitizenApisHolder apisHolder;

    public BrokerClient(CitizenApisHolder apisHolder) {
        this.apisHolder = apisHolder;
    }

    public BrokerInfoDTO getBrokerInfo(Long brokerId, String externalId, String accessToken){
        try{
            return apisHolder.getBrokerApi(accessToken).getBrokerInfo(brokerId, externalId);
        }catch (HttpClientErrorException.NotFound e){
            log.warn("Broker with brokerId {} or externalId {} not found", brokerId, externalId);
            return null;
        }

    }
}
