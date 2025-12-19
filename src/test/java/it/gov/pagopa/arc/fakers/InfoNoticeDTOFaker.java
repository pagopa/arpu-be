package it.gov.pagopa.arc.fakers;

import it.gov.pagopa.arc.model.generated.InfoNoticeDTO;
import it.gov.pagopa.arc.model.generated.UserDetailDTO;
import it.gov.pagopa.arc.model.generated.WalletInfoDTO;
import it.gov.pagopa.arc.utils.Constants;
import it.gov.pagopa.arc.utils.TestUtils;

import java.time.ZonedDateTime;

public class InfoNoticeDTOFaker {
    public static InfoNoticeDTO mockInstance(WalletInfoDTO walletInfo, UserDetailDTO payer){
        return mockInstanceBuilder(walletInfo, payer).build();
    }
    private static InfoNoticeDTO.InfoNoticeDTOBuilder mockInstanceBuilder(WalletInfoDTO walletInfo, UserDetailDTO payer){
        InfoNoticeDTO.InfoNoticeDTOBuilder infoNoticeDTOBuilder = InfoNoticeDTO.builder()
                .eventId("EVENT_ID")
                .authCode("250863")
                .rrn("51561651")
                .noticeDate(ZonedDateTime.parse("2024-06-27T13:07:25Z").withZoneSameInstant(Constants.ZONEID))
                .pspName("Worldline Merchant Services Italia S.p.A.")
                .walletInfo(walletInfo)
                .paymentMethod(InfoNoticeDTO.PaymentMethodEnum.PO)
                .payer(payer)
                .amount(565430L)
                .fee(29L)
                .totalAmount(565459L)
                .origin(InfoNoticeDTO.OriginEnum.UNKNOWN);

        TestUtils.assertNotNullFields(infoNoticeDTOBuilder);
        return infoNoticeDTOBuilder;
    }
}
