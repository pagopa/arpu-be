package it.gov.pagopa.arc.fakers;

import it.gov.pagopa.arc.model.generated.NoticeDTO;
import it.gov.pagopa.arc.utils.Constants;
import it.gov.pagopa.arc.utils.TestUtils;

import java.time.ZonedDateTime;

public class NoticeDTOFaker {
    public static NoticeDTO mockInstance(Integer bias, boolean isCart){
        return mockInstanceBuilder(bias, isCart).build();
    }
    public static NoticeDTO.NoticeDTOBuilder mockInstanceBuilder(Integer bias, boolean isCart) {
        if (!isCart) {
            NoticeDTO.NoticeDTOBuilder noticeDTOBuilder = NoticeDTO
                    .builder()
                    .eventId("EVENT_ID%d".formatted(bias))
                    .payeeName("PAYEE_NAME%d".formatted(bias))
                    .payeeTaxCode("PAYEE_TAX_CODE%d".formatted(bias))
                    .amount(268152L)
                    .noticeDate(ZonedDateTime.parse("2024-05-31T13:07:25Z").withZoneSameInstant(Constants.ZONEID))
                    .isCart(false)
                    .paidByMe(true)
                    .registeredToMe(true);

            TestUtils.assertNotNullFields(noticeDTOBuilder);
            return noticeDTOBuilder;
        } else {
            NoticeDTO.NoticeDTOBuilder noticeDTOBuilder = NoticeDTO
                    .builder()
                    .eventId("EVENT_ID%d".formatted(bias))
                    .payeeName("Pagamento Multiplo")
                    .payeeTaxCode("")
                    .noticeDate(ZonedDateTime.parse("2024-05-31T13:07:25Z"))
                    .isCart(true)
                    .paidByMe(true)
                    .registeredToMe(true);

            TestUtils.assertNotNullFields(noticeDTOBuilder);
            return noticeDTOBuilder;
        }
    }
}
