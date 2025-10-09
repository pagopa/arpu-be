package it.gov.pagopa.arc.dto.mapper.pullpayment;

import it.gov.pagopa.arc.connector.pullpayment.enums.PullPaymentOptionStatus;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PullPaymentOptionStatus2PaymentOptionStatusMapper {
    PaymentOptionStatus toPaymentOptionStatus(PullPaymentOptionStatus paymentOptionStatusSource);
}
