package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.client.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDetailsDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgClientTest {

    @Mock
    private CitizenApisHolder citizenApisHolderMock;
    @Mock
    private DebtPositionTypeOrgApi debtPositionTypeOrgApiMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgClient debtPositionTypeOrgClient;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgClient = new DebtPositionTypeOrgClient(citizenApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                citizenApisHolderMock,
                debtPositionTypeOrgApiMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenReturnOrganizationsWithSpontaneousDTO() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        Long brokerId = 1L;

        List expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
        Mockito.when(citizenApisHolderMock.getDebtPositionTypeOrgApi(accessToken)).thenReturn(debtPositionTypeOrgApiMock);
        Mockito.when(debtPositionTypeOrgApiMock.getDebtPositionTypeOrgsWithSpontaneous(brokerId,organizationId)).thenReturn(expectedResult);
        //when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneous(brokerId, organizationId, accessToken);
        //then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }

    @Test
    void givenOrganizationIdAndDebtPositionTypeOrgIdWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenReturnDebtPositionTypeOrgsWithSpontaneousDetailsDTO() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        DebtPositionTypeOrgsWithSpontaneousDetailsDTO expectedResult = podamFactory.manufacturePojo(DebtPositionTypeOrgsWithSpontaneousDetailsDTO.class);

        Mockito.when(citizenApisHolderMock.getDebtPositionTypeOrgApi(accessToken)).thenReturn(debtPositionTypeOrgApiMock);
        Mockito.when(debtPositionTypeOrgApiMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId)).thenReturn(expectedResult);
        //when
        DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId, accessToken);
        //then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }

    @Test
    void givenExceptionWhenGetDebtPositionTypeOrgsWithSpontaneousDetailThenReturnNull() {
        String accessToken = "accessToken";
        Long organizationId = 1L;
        Long debtPositionTypeOrgId = 1L;
        Long brokerId = 1L;

        Mockito.when(citizenApisHolderMock.getDebtPositionTypeOrgApi(accessToken)).thenReturn(debtPositionTypeOrgApiMock);

        Mockito.when(debtPositionTypeOrgApiMock.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        DebtPositionTypeOrgsWithSpontaneousDetailsDTO result = debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneousDetail(brokerId, organizationId, debtPositionTypeOrgId, accessToken);

        assertNull(result);
    }

    @Test
    void givenOrganizationIdWhenGetMostUsedSpontaneousDebtPositionTypeOrgsThenReturnList() {
        // given
        String accessToken = "ACCESS_TOKEN";
        Long organizationId = 1L;
        Long brokerId = 1L;
        OffsetDateTime offsetDateTimeFrom = OffsetDateTime.now().minusYears(1);
        OffsetDateTime offsetDateTimeTo= OffsetDateTime.now();
        Pageable pageable = Pageable.ofSize(10);

        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(
                citizenApisHolderMock
                        .getDebtPositionTypeOrgApi(accessToken)
        ).thenReturn(debtPositionTypeOrgApiMock);

        Mockito.when(
                debtPositionTypeOrgApiMock
                        .getMostUsedSpontaneousDebtPositionTypeOrgs(
                                brokerId,
                                organizationId,
                                offsetDateTimeFrom,
                                offsetDateTimeTo,
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                new ArrayList<>())
        ).thenReturn(expectedResult);

        // when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result =
                debtPositionTypeOrgClient
                        .getMostUsedSpontaneousDebtPositionTypeOrgs(brokerId, organizationId, offsetDateTimeFrom, offsetDateTimeTo, pageable, accessToken);

        // then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

}