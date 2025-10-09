package it.gov.pagopa.arc.dto.mapper.bizevents.paidnotice;

import it.gov.pagopa.arc.connector.bizevents.dto.paidnotice.BizEventsInfoPaidNoticeDTO;
import it.gov.pagopa.arc.connector.bizevents.enums.Origin;
import it.gov.pagopa.arc.dto.mapper.BizEventsUserDetail2UserDetailDTOMapper;
import it.gov.pagopa.arc.dto.mapper.BizEventsWalletInfo2WalletInfoDTOMapper;
import it.gov.pagopa.arc.dto.mapper.MapperUtilities;
import it.gov.pagopa.arc.model.generated.InfoNoticeDTO;
import org.mapstruct.*;


@Mapper(componentModel = "spring", uses = {MapperUtilities.class, BizEventsWalletInfo2WalletInfoDTOMapper.class, BizEventsUserDetail2UserDetailDTOMapper.class})
public interface BizEventsInfoPaidNoticeDTO2InfoNoticeDTOMapper {

    @Mapping(source = "amount", target = "amount", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName = "euroToCents")
    @Mapping(source = "fee", target = "fee", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName = "euroToCents", defaultValue = "0L")
    @Mapping(source = "noticeDate", target = "noticeDate", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, qualifiedByName = "dateStringToZonedDateTime")
    @Mapping(source = "walletInfo", target = "walletInfo", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(source = "payer", target = "payer", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "totalAmount", source = "bizEventsInfoPaidNoticeDTO")
    @Mapping(target = "origin", source = "origin", qualifiedByName = "mapBizOrigin")
    InfoNoticeDTO toInfoNoticeDTO(BizEventsInfoPaidNoticeDTO bizEventsInfoPaidNoticeDTO);

    @Named("mapBizOrigin")
    @ValueMapping(target = "NDP001_PROD", source = "NDP001PROD")
    @ValueMapping(target = "NDP002_PROD", source = "NDP002PROD")
    @ValueMapping(target = "NDP003_PROD", source = "NDP003PROD")
    InfoNoticeDTO.OriginEnum toOrigin(Origin origin);

    default Long calculateTotalAmount(BizEventsInfoPaidNoticeDTO bizEventsInfoPaidNoticeDTO){
        String bizEventsAmount = bizEventsInfoPaidNoticeDTO.getAmount();
        String bizEventsFee = bizEventsInfoPaidNoticeDTO.getFee();
        Long fee = 0L;

        if (bizEventsAmount != null){
            Long amount = MapperUtilities.euroToCents(bizEventsAmount);
            if (bizEventsFee != null){
                fee = MapperUtilities.euroToCents(bizEventsFee);
            }

            return MapperUtilities.calculateTotalAmount(amount, fee);
        }
        return null;
    }
}
