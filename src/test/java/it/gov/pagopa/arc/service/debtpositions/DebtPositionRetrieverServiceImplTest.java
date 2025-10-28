package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.connector.citizen.DebtPositionService;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionRetrieverServiceImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private DebtPositionService debtPositionServiceMock;
    private DebtPositionRetrieverService debtPositionRetrieverService;

    @BeforeEach
    void setUp() {
        debtPositionRetrieverService = new DebtPositionRetrieverServiceImpl(debtPositionServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(debtPositionServiceMock);
    }

    @Test
    void whenGetUnpaidPaymentNoticeZipThenOk() {
        //given
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        FileResourceDTO expectedResult = podamFactory.manufacturePojo(FileResourceDTO.class);

        Mockito.when(debtPositionServiceMock.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode)).thenReturn(expectedResult);

        FileResourceDTO result = debtPositionRetrieverService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, loggedUser);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenCreateSpontaneousDebtPositionThenOk() {
        //given
        Long brokerId = 1L;
        DebtPositionRequestDTO requestDTO = podamFactory.manufacturePojo(DebtPositionRequestDTO.class);
        DebtPositionResponseDTO expectedResult = podamFactory.manufacturePojo(DebtPositionResponseDTO.class);

        Mockito.when(debtPositionServiceMock.createSpontaneousDebtPosition(brokerId, requestDTO)).thenReturn(expectedResult);
        //when

        DebtPositionResponseDTO result = debtPositionRetrieverService.createSpontaneousDebtPosition(brokerId, requestDTO);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

}