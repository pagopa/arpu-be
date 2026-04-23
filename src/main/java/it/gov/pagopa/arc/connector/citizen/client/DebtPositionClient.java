package it.gov.pagopa.arc.connector.citizen.client;


import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.utils.PageUtils;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
public class DebtPositionClient {

    private final CitizenApisHolder apisHolder;

    public DebtPositionClient(CitizenApisHolder apisHolder) {
        this.apisHolder = apisHolder;
    }

  public FileResourceDTO getUnpaidPaymentNoticeZip(Long brokerId, Long debtPositionId, String fiscalCode, String accessToken) {
        try{
            ResponseEntity<Resource> resourceResponseEntity = apisHolder.getDebtPositionApi(accessToken)
                .getUnpaidPaymentNoticeZipWithHttpInfo(brokerId,fiscalCode,debtPositionId);
            return FileResourceDTO.builder()
                .resource(resourceResponseEntity.getBody())
                .fileName(resourceResponseEntity.getHeaders().getContentDisposition().getFilename())
                .build();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("DebtPosition having brokerId {} and debtPositionId {} not found", brokerId, debtPositionId);
            return null;
        }
  }

  public DebtPositionResponseDTO createSpontaneousDebtPosition(Long brokerId, DebtPositionRequestDTO body, String accessToken){
        return apisHolder.getDebtPositionApi(accessToken).createSpontaneousDebtPosition(brokerId, body);
  }

  public DebtPositionExtendedDTO getDebtPositionDetail(Long brokerId, Long debtPositionId, String fiscalCode, String accessToken){
        try{
            return apisHolder.getDebtPositionApi(accessToken).getDebtPositionDetail(brokerId, debtPositionId, fiscalCode);
        }catch (HttpClientErrorException.NotFound e){
            log.warn("DebtPosition with debtPositionId {} not found", debtPositionId);
            return null;
        }

  }

  public FileResourceDTO getPaymentNotice(String fiscalCode, Long brokerId, Long organizationId, String nav, String accessToken) {
        try{
            ResponseEntity<Resource> resourceResponseEntity = apisHolder.getDebtPositionApi(accessToken)
                .getPaymentNoticeWithHttpInfo(fiscalCode, brokerId, organizationId, nav);
            return FileResourceDTO.builder()
                .resource(resourceResponseEntity.getBody())
                .fileName(resourceResponseEntity.getHeaders().getContentDisposition().getFilename())
                .build();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("paymentNotice having brokerId {} and organizationId {} not found", brokerId, organizationId);
            return null;
        }
    }

    public PagedDebtorDebtPositionDTO getPagedDebtorDebtPosition(String fiscalCode, Long brokerId, String orgName, String orgFiscalCode, Pageable pageable, String accessToken){
        return apisHolder.getDebtPositionApi(accessToken).getPagedUnpaidDebtPositions(
                fiscalCode,
                brokerId,
                orgName,
                orgFiscalCode,
                PageUtils.getPageNumber(pageable),
                PageUtils.getPageSize(pageable),
                PageUtils.getSortList(pageable));
    }

    public DebtorUnpaidDebtPositionOverviewDTO getDebtorUnpaidDebtPositionOverview(Long brokerId, Long debtPositionId, String xFiscalCode, Long organizationId, String accessToken){
        try {
            return apisHolder.getDebtPositionApi(accessToken)
                    .getDebtorUnpaidDebtPositionOverview(brokerId, debtPositionId, xFiscalCode, organizationId);
        }catch (HttpClientErrorException.NotFound e){
            log.warn("DebtorUnpaidDebtPositionOverview having debtPositionId {} with brokerId {} and organizationId {} not found", debtPositionId, brokerId, organizationId);
            return null;
        }
    }
}
