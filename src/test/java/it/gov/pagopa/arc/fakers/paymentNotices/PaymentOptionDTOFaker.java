package it.gov.pagopa.arc.fakers.paymentNotices;

import it.gov.pagopa.arc.model.generated.InstallmentDTO;
import it.gov.pagopa.arc.model.generated.PaymentOptionDTO;
import it.gov.pagopa.arc.utils.Constants;

import java.time.ZonedDateTime;
import java.util.List;

public class PaymentOptionDTOFaker {
    public static PaymentOptionDTO mockInstance(InstallmentDTO installmentDTO, Boolean paymentInstallments){
        return mockInstanceBuilder(installmentDTO, paymentInstallments).build();
    }
    public static PaymentOptionDTO.PaymentOptionDTOBuilder mockInstanceBuilder(InstallmentDTO installment,  Boolean paymentInstallments){
        PaymentOptionDTO.PaymentOptionDTOBuilder paymentOptionDTO = PaymentOptionDTO.builder()
                .description("Test Pull - unica opzione")
                .numberOfInstallments(1)
                .amount(120L)
                .dueDate(ZonedDateTime.parse("2024-10-30T23:59:59Z").withZoneSameInstant(Constants.ZONEID))
                .isPartialPayment(false)
                .installments(List.of(installment));

        if(paymentInstallments){
            paymentOptionDTO.description("Test Pull - piano rateale");
            installment.setDescription("Test Pull - piano rateale");
            paymentOptionDTO.numberOfInstallments(2).installments(List.of(installment,installment));
        }

        return paymentOptionDTO;
    }
}
