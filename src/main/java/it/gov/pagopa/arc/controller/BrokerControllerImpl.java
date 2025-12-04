package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.BrokerApi;
import it.gov.pagopa.arc.service.broker.BrokerFacadeService;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BrokerControllerImpl implements BrokerApi {

    private final BrokerFacadeService brokerFacadeService;

    public BrokerControllerImpl(BrokerFacadeService brokerFacadeService) {
        this.brokerFacadeService = brokerFacadeService;
    }

    @Override
    public ResponseEntity<BrokerInfoDTO> getPublicBrokerInfo(Long brokerId) {
        log.info("Requested getPublicBrokerInfo on brokerId {}", brokerId);
        return ResponseEntity.ofNullable(brokerFacadeService.getBrokerInfo(brokerId));
    }
}
