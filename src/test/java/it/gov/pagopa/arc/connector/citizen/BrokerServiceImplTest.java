package it.gov.pagopa.arc.connector.citizen;

import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.connector.citizen.client.BrokerClient;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
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
class BrokerServiceImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private AuthnService authnServiceMock;
    @Mock
    private BrokerClient brokerClientMock;
    private BrokerService brokerService;

    @BeforeEach
    void setUp() {
        brokerService = new BrokerServiceImpl(brokerClientMock,authnServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                authnServiceMock,
                brokerClientMock
        );
    }

    @Test
    void whenGetBrokerInfoThenInvokeClient() {
        String accessToken = "accessToken";
        Long brokerId = 1L;
        BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

        Mockito.when(authnServiceMock.getAccessToken()).thenReturn(accessToken);
        Mockito.when(brokerClientMock.getBrokerInfo(brokerId,accessToken)).thenReturn(expectedResult);

        BrokerInfoDTO result = brokerService.getBrokerInfo(brokerId);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }
}