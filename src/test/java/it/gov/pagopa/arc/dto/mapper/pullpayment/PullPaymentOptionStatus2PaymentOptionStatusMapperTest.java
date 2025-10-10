package it.gov.pagopa.arc.dto.mapper.pullpayment;

import it.gov.pagopa.arc.connector.pullpayment.enums.PullPaymentOptionStatus;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PullPaymentOptionStatus2PaymentOptionStatusMapperTest {
    private final PullPaymentOptionStatus2PaymentOptionStatusMapper paymentOptionStatusMapper = Mappers.getMapper(PullPaymentOptionStatus2PaymentOptionStatusMapper.class);
    @Test
    void givenPullPaymentOptionStatusWhenCallMapperThenReturnPaymentOptionStatus() {
        //given
        PullPaymentOptionStatus pullPaymentOptionStatus = PullPaymentOptionStatus.PO_PAID;
        //when
        PaymentOptionStatus result = paymentOptionStatusMapper.toPaymentOptionStatus(pullPaymentOptionStatus);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(PaymentOptionStatus.PO_PAID, result);
    }
}