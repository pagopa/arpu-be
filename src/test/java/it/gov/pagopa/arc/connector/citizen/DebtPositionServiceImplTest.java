package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.DebtPositionClient;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionRequestDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DebtPositionServiceImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private DebtPositionClient debtPositionClientMock;
    private DebtPositionService debtPositionService;

    @BeforeEach
    void setUp() {
        debtPositionService = new DebtPositionServiceImpl(authnServiceMock,debtPositionClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                debtPositionClientMock
        );
    }

    @Test
    void whenGetUnpaidPaymentNoticeZipThenOk() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        String fiscalCode = "fiscalCode";
        FileResourceDTO expectedResult = podamFactory.manufacturePojo(FileResourceDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionClientMock.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, accessToken)).thenReturn(expectedResult);

        FileResourceDTO result = debtPositionService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void givenBrokerIdAndDebtPositionRequestDTOWhenCreateSpontaneousDebtPositionThenReturnDebtPositionResponseDTO() {
        //given
        String accessToken = "accessToken";
        Long brokerId = 1L;
        DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
        DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(debtPositionClientMock.createSpontaneousDebtPosition(brokerId, requestDTO, accessToken)).thenReturn(expectedResult);
        //when
        DebtPositionResponseDTO result = debtPositionService.createSpontaneousDebtPosition(brokerId, requestDTO);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}