package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.InstallmentClient;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentServiceImpl implements InstallmentService {
    private final AuthnService authnService;
    private final InstallmentClient installmentClient;

    public InstallmentServiceImpl(AuthnService authnService, InstallmentClient installmentClient) {
        this.authnService = authnService;
        this.installmentClient = installmentClient;
    }

    @Override
    public List<InstallmentDebtorExtendedDTO> getInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode) {
        return installmentClient.getInstallmentsByIuvOrNav(brokerId,iuvOrNav, debtorFiscalCode,orgFiscalCode,authnService.getAccessToken());
    }
}
