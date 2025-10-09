package it.gov.pagopa.arc.dto.mapper.gpd;

import it.gov.pagopa.arc.connector.gpd.enums.GPDPaymentOptionDetailsStatus;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

import java.util.stream.Stream;

class GPDPaymentOptionDetailsStatus2PaymentOptionStatusMapperTest {
    private final GPDPaymentOptionDetailsStatus2PaymentOptionStatusMapper mapper = Mappers.getMapper(GPDPaymentOptionDetailsStatus2PaymentOptionStatusMapper.class);

    @ParameterizedTest
    @MethodSource("provideEnumMappings")
    void givenGPDPaymentOptionDetailsStatusWhenToPaymentOptionStatusThenReturnPaymentOptionStatus(GPDPaymentOptionDetailsStatus gpdPaymentOptionDetailsStatus, PaymentOptionStatus paymentOptionStatus) {
        //when
        PaymentOptionStatus result = mapper.toPaymentOptionStatus(gpdPaymentOptionDetailsStatus);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(paymentOptionStatus, result);
    }

    private static Stream<Arguments> provideEnumMappings() {
        return Stream.of(
                Arguments.of(GPDPaymentOptionDetailsStatus.PO_UNPAID, PaymentOptionStatus.PO_UNPAID),
                Arguments.of(GPDPaymentOptionDetailsStatus.PO_PAID, PaymentOptionStatus.PO_PAID),
                Arguments.of(GPDPaymentOptionDetailsStatus.PO_PARTIALLY_REPORTED, PaymentOptionStatus.PO_PARTIALLY_REPORTED),
                Arguments.of(GPDPaymentOptionDetailsStatus.PO_REPORTED, PaymentOptionStatus.PO_REPORTED)
        );
    }
}