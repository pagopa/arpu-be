package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.citizen.client.DebtPositionTypeOrgClient;
import it.gov.pagopa.arc.utils.TestUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DebtPositionTypeOrgServiceImplTest {

    @Mock
    private DebtPositionTypeOrgClient debtPositionTypeOrgClientMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private DebtPositionTypeOrgService debtPositionTypeOrgService;

    @BeforeEach
    void setUp() {
        debtPositionTypeOrgService = new DebtPositionTypeOrgServiceImpl(debtPositionTypeOrgClientMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                debtPositionTypeOrgClientMock
        );
    }

    @Test
    void givenOrganizationIdWhenGetDebtPositionTypeOrgsWithSpontaneousThenDebtPositionTypeOrgsWithSpontaneous() {
        //given
        String accessToken = "accessToken";
        Long organizationId = 1L;
        List<DebtPositionTypeOrgsWithSpontaneousDTO> expectedResult = podamFactory.manufacturePojo(List.class, DebtPositionTypeOrgsWithSpontaneousDTO.class);

        Mockito.when(debtPositionTypeOrgClientMock.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken)).thenReturn(expectedResult);
        //when
        List<DebtPositionTypeOrgsWithSpontaneousDTO> result = debtPositionTypeOrgService.getDebtPositionTypeOrgsWithSpontaneous(organizationId, accessToken);
        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}