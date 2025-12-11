package it.gov.pagopa.arc.connector.citizen.client;


import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class InstallmentClient {
    private final CitizenApisHolder apisHolder;

    public InstallmentClient(CitizenApisHolder apisHolder) {
        this.apisHolder = apisHolder;
    }

    public List<InstallmentDebtorExtendedDTO> getInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode, String accessToken) {
        try{
            return apisHolder.getInstallmentApi(accessToken).getInstallmentsByIuvOrNav(brokerId,iuvOrNav,debtorFiscalCode,orgFiscalCode);
        }catch (HttpClientErrorException.NotFound e){
            log.warn("Installments having brokerId {} and iuvOrNav {} not found", brokerId, iuvOrNav);
            return Collections.emptyList();
        }
    }
}
