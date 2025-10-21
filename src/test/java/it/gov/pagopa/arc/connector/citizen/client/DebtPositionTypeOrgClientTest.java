package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.citizen.dto.generated.DebtPositionTypeOrgsWithSpontaneousDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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

        List expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);
        Mockito.when(citizenApisHolderMock.getDebtPositionTypeOrgApi(accessToken)).thenReturn(debtPositionTypeOrgApiMock);
        Mockito.when(debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken)).thenReturn(expectedResult);
        //when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgClient.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken);
        //then
        assertNotNull(result);
        assertSame(expectedResult, result);
    }
}