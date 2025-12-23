package it.gov.pagopa.arc.fakers.connector;

import it.gov.pagopa.arc.model.generated.PaymentOptionDetailsDTO;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import it.gov.pagopa.arc.utils.Constants;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentOptionDetailsDTOFaker {

    public static List<PaymentOptionDetailsDTO> mockInstance(Integer bias, Boolean isPartialPayment) {
        List<PaymentOptionDetailsDTO> paymentOptionDetailsDTOList = new ArrayList<>();

        paymentOptionDetailsDTOList.add(mockInstanceBuilder(bias, isPartialPayment).build());
        if (isPartialPayment){
            paymentOptionDetailsDTOList.add(mockInstanceBuilder(bias+1, isPartialPayment).build());
        }
        return paymentOptionDetailsDTOList;
    }

    public static PaymentOptionDetailsDTO.PaymentOptionDetailsDTOBuilder mockInstanceBuilder(Integer bias, Boolean isPartialPayment) {
        PaymentOptionDetailsDTO.PaymentOptionDetailsDTOBuilder paymentOptionDetailsDTO = getPaymentOptionDetailsDTOBuilder(bias);

        if (isPartialPayment) {
            paymentOptionDetailsDTO
                    .isPartialPayment(true)
                    .description("Installments Payment");
        }

        return paymentOptionDetailsDTO;
    }

    private static PaymentOptionDetailsDTO.PaymentOptionDetailsDTOBuilder getPaymentOptionDetailsDTOBuilder(Integer bias) {
        return PaymentOptionDetailsDTO.builder()
                .nav("TEST_NAV%d".formatted(bias))
                .iuv("TEST_IUV%d".formatted(bias))
                .amount(1000L)
                .description("Single Payment")
                .isPartialPayment(false)
                .dueDate(ZonedDateTime.parse("2024-10-30T23:59:59Z").withZoneSameInstant(Constants.ZONEID))
                .notificationFee(2L)
                .status(PaymentOptionStatus.PO_UNPAID);
    }
}
