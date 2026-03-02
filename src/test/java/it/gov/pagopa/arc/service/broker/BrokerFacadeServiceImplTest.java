package it.gov.pagopa.arc.service.broker;

import it.gov.pagopa.arc.connector.citizen.BrokerService;
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
class BrokerFacadeServiceImplTest {

    @Mock
    private BrokerService brokerServiceMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private BrokerFacadeService brokerFacadeService;

    @BeforeEach
    void setUp() {
        brokerFacadeService = new BrokerFacadeServiceImpl(brokerServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                brokerServiceMock
        );
    }

    @Test
    void whenGetBrokerInfoThenOk() {
        Long brokerId = 1L;
        String externalId = "externalId";
        BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

        Mockito.when(brokerServiceMock.getBrokerInfo(brokerId, externalId)).thenReturn(expectedResult);

        BrokerInfoDTO result = brokerFacadeService.getBrokerInfo(brokerId, externalId);

        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

}