package it.gov.pagopa.arc.dto.mapper.gpd;

import it.gov.pagopa.arc.connector.gpd.dto.GPDPaymentOptionDetailsDTO;
import it.gov.pagopa.arc.connector.gpd.enums.GPDPaymentOptionDetailsStatus;
import it.gov.pagopa.arc.fakers.connector.gpd.GPDPaymentOptionDetailsDTOFaker;
import it.gov.pagopa.arc.model.generated.PaymentOptionDetailsDTO;
import it.gov.pagopa.arc.model.generated.PaymentOptionStatus;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GPDPaymentOptionDetailsDTO2PaymentOptionDetailsDTOMapperTest {
    @Mock
    private GPDPaymentOptionDetailsStatus2PaymentOptionStatusMapper gpdPaymentOptionDetailsStatus2PaymentOptionStatusMapperMock;

    @InjectMocks
    GPDPaymentOptionDetailsDTO2PaymentOptionDetailsDTOMapper mapper = Mappers.getMapper(GPDPaymentOptionDetailsDTO2PaymentOptionDetailsDTOMapper.class);
    @Test
    void givenToPaymentOptionDetailsDTOWhenThen() {
        //given
        List<GPDPaymentOptionDetailsDTO> gpdPaymentOptionDetailsDTO = GPDPaymentOptionDetailsDTOFaker.mockInstance(1, false);
        Mockito.when(gpdPaymentOptionDetailsStatus2PaymentOptionStatusMapperMock.toPaymentOptionStatus(GPDPaymentOptionDetailsStatus.PO_UNPAID)).thenReturn(PaymentOptionStatus.PO_UNPAID);

        //when
        PaymentOptionDetailsDTO result = mapper.toPaymentOptionDetailsDTO(gpdPaymentOptionDetailsDTO.getFirst());
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(gpdPaymentOptionDetailsDTO.getFirst().getNav() , result.getNav());
        Assertions.assertEquals(gpdPaymentOptionDetailsDTO.getFirst().getIuv() , result.getIuv());
        Assertions.assertEquals(gpdPaymentOptionDetailsDTO.getFirst().getAmount(), result.getAmount());
        Assertions.assertEquals(gpdPaymentOptionDetailsDTO.getFirst().getDescription(), result.getDescription());
        Assertions.assertEquals(gpdPaymentOptionDetailsDTO.getFirst().getIsPartialPayment(), result.getIsPartialPayment());
        Assertions.assertEquals(ZonedDateTime.parse("2024-10-30T23:59:59Z"), result.getDueDate());
        Assertions.assertEquals(gpdPaymentOptionDetailsDTO.getFirst().getNotificationFee(), result.getNotificationFee());
        Assertions.assertEquals(PaymentOptionStatus.PO_UNPAID, result.getStatus());

        TestUtils.assertNotNullFields(result);

    }
}