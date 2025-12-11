package it.gov.pagopa.arc.service.installment;

import it.gov.pagopa.arc.connector.citizen.InstallmentService;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentDebtorExtendedDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstallmentFacadeServiceImpl implements InstallmentFacadeService {

    private final InstallmentService installmentService;

    public InstallmentFacadeServiceImpl(InstallmentService installmentService) {
        this.installmentService = installmentService;
    }

    @Override
    public List<InstallmentDebtorExtendedDTO> getPublicInstallmentsByIuvOrNav(Long brokerId, String iuvOrNav, String debtorFiscalCode, String orgFiscalCode) {
        return installmentService.getInstallmentsByIuvOrNav(brokerId, iuvOrNav, debtorFiscalCode, orgFiscalCode);
    }
}
