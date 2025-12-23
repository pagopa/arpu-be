package it.gov.pagopa.arc.fakers.paymentNotices;

import it.gov.pagopa.arc.model.generated.InstallmentDTO;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import it.gov.pagopa.arc.utils.Constants;

import java.time.ZonedDateTime;

public class InstallmentDTOFaker {
    public static InstallmentDTO mockInstance(){
        return mockInstanceBuilder().build();
    }

    public static InstallmentDTO.InstallmentDTOBuilder mockInstanceBuilder(){
        return InstallmentDTO.builder()
                .nav("347000000880099993")
                .iuv("47000000880099993")
                .paTaxCode("99999000013")
                .paFullName("EC Demo Pagamenti Pull Test")
                .amount(120L)
                .description("Test Pull - unica opzione")
                .dueDate(ZonedDateTime.parse("2024-10-30T23:59:59Z").withZoneSameInstant(Constants.ZONEID))
                .status(PaymentOptionStatus.PO_UNPAID);
    }
}
