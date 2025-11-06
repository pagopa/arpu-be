package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.PageUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ReceiptClient {
    
    private final CitizenApisHolder citizenApisHolder;

    public ReceiptClient(CitizenApisHolder citizenApisHolder) {
        this.citizenApisHolder = citizenApisHolder;
    }

    public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, String orgName, Pageable pageable, String accessToken){
        return citizenApisHolder.getReceiptApi(accessToken)
                .getPagedDebtorReceipts(
                        brokerId,
                        debtorFiscalCode,
                        orgName,
                        PageUtils.getPageNumber(pageable),
                        PageUtils.getPageSize(pageable),
                        PageUtils.getSortList(pageable)
                        );
    }
}
