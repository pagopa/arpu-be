package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.PageUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
public class DebtPositionTypeOrgClient {

    private final CitizenApisHolder citizenApisHolder;

    public DebtPositionTypeOrgClient(CitizenApisHolder citizenApisHolder) {
        this.citizenApisHolder = citizenApisHolder;
    }

    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getDebtPositionTypeOrgsWithSpontaneous(Long brokerId, Long organizationId, String accessToken){
        return citizenApisHolder.getDebtPositionTypeOrgApi(accessToken)
                .getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId);
    }

    public DebtPositionTypeOrgsWithSpontaneousDetailsDTO getDebtPositionTypeOrgsWithSpontaneousDetail(Long brokerId, Long organizationId, Long debtPositionTypeOrgId, String accessToken){
        try {
            return citizenApisHolder.getDebtPositionTypeOrgApi(accessToken).getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId);
        }catch (HttpClientErrorException.NotFound e) {
            log.warn("DebtPositionTypeOrgsWithSpontaneousDetails with debtPositionTypeOrgId {} not found", debtPositionTypeOrgId);
            return null;
        }
    }

    public List<DebtPositionTypeOrgsWithSpontaneousDTO> getMostUsedSpontaneousDebtPositionTypeOrgs(Long brokerId, Long organizationId, OffsetDateTime creationDateFrom, OffsetDateTime creationDateTo, Pageable pageable, String accessToken){
        return citizenApisHolder.getDebtPositionTypeOrgApi(accessToken)
                .getMostUsedSpontaneousDebtPositionTypeOrgs(brokerId,
                        organizationId,
                        creationDateFrom,
                        creationDateTo,
                        PageUtils.getPageNumber(pageable),
                        PageUtils.getPageSize(pageable),
                        PageUtils.getSortList(pageable));
    }

}
