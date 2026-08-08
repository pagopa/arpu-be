package it.gov.pagopa.arc.service.debtpositions;

import it.gov.pagopa.arc.connector.citizen.DebtPositionService;
import it.gov.pagopa.arc.dto.FileResourceDTO;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.exception.common.NotFoundException;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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

        when(debtPositionServiceMock.getUnpaidPaymentNoticeZip(brokerId, debtPositionId, fiscalCode)).thenReturn(expectedResult);

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

        when(debtPositionServiceMock.createSpontaneousDebtPosition(brokerId, requestDTO)).thenReturn(expectedResult);
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
        DebtPositionExtendedDTO expectedResult = podamFactory.manufacturePojo(DebtPositionExtendedDTO.class);

        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        when(debtPositionServiceMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode)).thenReturn(expectedResult);
        //when
        DebtPositionExtendedDTO result = debtPositionFacadeService.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, loggedUser);
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
        when(debtPositionServiceMock.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> debtPositionFacadeService.getDebtPositionDetail(brokerId, debtPositionId, fiscalCode, loggedUser));

    }

    @Test
    void whenGetPaymentNoticeThenOk() {
        //given
        String fiscalCode = "fiscalCode";
        Long brokerId = 1L;
        Long organizationId = 2L;
        String nav = "nav";
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        FileResourceDTO expectedResult = podamFactory.manufacturePojo(FileResourceDTO.class);

        when(debtPositionServiceMock.getPaymentNotice(fiscalCode,brokerId,organizationId,nav)).thenReturn(expectedResult);

        FileResourceDTO result = debtPositionFacadeService.getPaymentNotice(fiscalCode,brokerId,organizationId,nav, loggedUser);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenGetPagedUnpaidDebtPositionsThenOk() {
        //given
        Long brokerId = 1L;
        String orgName = "orgName";
        String orgFiscalCode = "orgFiscalCode";
        String fiscalCode = "fiscalCode";

        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        PagedDebtorDebtPositionDTO expectedResult = podamFactory.manufacturePojo(PagedDebtorDebtPositionDTO.class);

        when(debtPositionServiceMock.getPagedDebtorDebtPosition(fiscalCode, brokerId, orgName, orgFiscalCode, Pageable.ofSize(1))).thenReturn(expectedResult);
        //when
        PagedDebtorDebtPositionDTO result= debtPositionFacadeService.getPagedUnpaidDebtPositions(brokerId, fiscalCode, orgName, orgFiscalCode, Pageable.ofSize(1), loggedUser);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @Test
    void whenGetDebtorUnpaidDebtPositionOverviewThenOk() {
        // given
        Long brokerId = 1L;
        Long debtPositionId = 2L;
        String xFiscalCode = "fiscalCode";
        Long organizationId = 3L;
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

        DebtorUnpaidDebtPositionOverviewDTO expectedResult =
                podamFactory.manufacturePojo(DebtorUnpaidDebtPositionOverviewDTO.class);

        when(debtPositionServiceMock.getDebtorUnpaidDebtPositionOverview(
                        brokerId, debtPositionId, xFiscalCode, organizationId))
                .thenReturn(expectedResult);

        // when
        DebtorUnpaidDebtPositionOverviewDTO result = debtPositionFacadeService.getDebtorUnpaidDebtPositionOverview(
                brokerId, debtPositionId, xFiscalCode, organizationId, loggedUser);

        // then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

}