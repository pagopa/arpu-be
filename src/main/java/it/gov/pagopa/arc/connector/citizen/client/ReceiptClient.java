package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.DebtorReceiptsFiltersDTO;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.utils.PageUtils;
import it.gov.pagopa.pu.citizen.dto.generated.PagedDebtorReceiptsDTO;
import it.gov.pagopa.pu.citizen.dto.generated.ReceiptDetailExtendedDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@Service
public class ReceiptClient {
    
    private final CitizenApisHolder citizenApisHolder;

    public ReceiptClient(CitizenApisHolder citizenApisHolder) {
        this.citizenApisHolder = citizenApisHolder;
    }

    public PagedDebtorReceiptsDTO getPagedDebtorReceipts(Long brokerId, String debtorFiscalCode, DebtorReceiptsFiltersDTO debtorReceiptsFiltersDTO, Pageable pageable, String accessToken){
        return citizenApisHolder.getReceiptApi(accessToken)
                .getPagedDebtorReceipts(
                        brokerId,
                        debtorFiscalCode,
                        debtorReceiptsFiltersDTO.getOrgName(),
                        debtorReceiptsFiltersDTO.getNoticeNumberOrIuv(),
                        debtorReceiptsFiltersDTO.getPaymentDateTimeFrom(),
                        debtorReceiptsFiltersDTO.getPaymentDateTimeTo(),
                        PageUtils.getPageNumber(pageable),
                        PageUtils.getPageSize(pageable),
                        PageUtils.getSortList(pageable)
                        );
    }

    public ReceiptDetailExtendedDTO getReceiptDetail(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, String accessToken) {
        try {
            return citizenApisHolder.getReceiptApi(accessToken)
                    .getReceiptDetail(debtorFiscalCode,brokerId, organizationId,receiptId);
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("ReceiptDetail with receiptId {} brokerId {} and organizationId {} not found", receiptId, brokerId, organizationId);
            return null;
        }
    }

    public FileResourceDTO getReceiptPdf(Long brokerId, Long organizationId, Long receiptId, String debtorFiscalCode, String accessToken) {
        try {
            ResponseEntity<Resource> resourceResponseEntity = citizenApisHolder.getReceiptApi(accessToken)
                    .getReceiptPdfWithHttpInfo(debtorFiscalCode,brokerId,organizationId,receiptId);
            return FileResourceDTO.builder()
                    .resource(resourceResponseEntity.getBody())
                    .fileName(resourceResponseEntity.getHeaders().getContentDisposition().getFilename())
                    .build();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("receipt with receiptId {} brokerId {} and organizationId {} not found", receiptId, brokerId, organizationId);
            return null;
        }
    }
}
