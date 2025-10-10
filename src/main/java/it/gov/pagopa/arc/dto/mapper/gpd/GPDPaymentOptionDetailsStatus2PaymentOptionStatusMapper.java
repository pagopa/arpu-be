package it.gov.pagopa.arc.dto.mapper.gpd;

import it.gov.pagopa.arc.connector.gpd.enums.GPDPaymentOptionDetailsStatus;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GPDPaymentOptionDetailsStatus2PaymentOptionStatusMapper {

    PaymentOptionStatus toPaymentOptionStatus(GPDPaymentOptionDetailsStatus gpdPaymentOptionDetailsStatus);
}
