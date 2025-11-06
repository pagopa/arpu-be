package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.connector.citizen.DebtPositionService;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.exception.custom.ResourceNotFoundException;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionDTO;
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
class DebtPositionFacadeServiceImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private DebtPositionService debtPositionServiceMock;
    private DebtPositionFacadeService debtPositionFacadeService;

    @BeforeEach
    void setUp() {
        debtPositionFacadeService = new DebtPositionFacadeServiceImpl(debtPositionServiceMock);
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

        FileResourceDTO result = debtPositionFacadeService.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode, loggedUser);

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

        DebtPositionResponseDTO result = debtPositionFacadeService.createSpontaneousDebtPosition(brokerId, requestDTO);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenGetDebtPositionDetailThenOk() {
        //given
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        DebtPositionDTO expectedResult = podamFactory.manufacturePojo(DebtPositionDTO.class);

        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        Mockito.when(debtPositionServiceMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode)).thenReturn(expectedResult);
        //when
        DebtPositionDTO result = debtPositionFacadeService.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, loggedUser);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void givenNullDebtPositionWhenGetDebtPositionDetailThenThrowException() {
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long debtPositionId = 2L;

        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        Mockito.when(debtPositionServiceMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> debtPositionFacadeService.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, loggedUser));

    }

    @Test
    void whenGetPaymentNoticeThenOk() {
        //given
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        Long installmentId = 3L;
        String iuv = "iuv";
        String iud = "iud";
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        FileResourceDTO expectedResult = podamFactory.manufacturePojo(FileResourceDTO.class);

        Mockito.when(debtPositionServiceMock.getPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud)).thenReturn(expectedResult);

        FileResourceDTO result = debtPositionFacadeService.getPaymentNotice(fiscalCode,brokerId,organizationId,installmentId,iuv,iud, loggedUser);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}