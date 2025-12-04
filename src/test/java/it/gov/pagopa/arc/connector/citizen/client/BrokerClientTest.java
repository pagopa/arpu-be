package it.gov.pagopa.arc.connector.citizen.client;

import it.gov.pagopa.arc.connector.citizen.config.CitizenApisHolder;
import it.gov.pagopa.pu.citizen.controller.generated.BrokerApi;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@ExtendWith(MockitoExtension.class)
class BrokerClientTest {
    @Mock
    private CitizenApisHolder citizenApisHolderMock;
    @Mock
    private BrokerApi brokerApiMock;
    private BrokerClient brokerClient;

    @BeforeEach
    void setUp() {
        brokerClient = new BrokerClient(citizenApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                citizenApisHolderMock,
                brokerApiMock
        );
    }

    @Test
    void whenGetBrokerInfoThenOk(){
        String accessToken = "accessToken";
        Long brokerId = 1L;
        BrokerInfoDTO expectedResource = new BrokerInfoDTO();

        Mockito.when(citizenApisHolderMock.getBrokerApi(accessToken)).thenReturn(brokerApiMock);
        Mockito.when(brokerApiMock.getBrokerInfo(brokerId)).thenReturn(expectedResource);

        BrokerInfoDTO response = brokerClient.getBrokerInfo(brokerId,accessToken);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResource,response);
    }

    @Test
    void givenNotFoundWhenGetBrokerInfoThenNull(){
        String accessToken = "accessToken";
        Long brokerId = 1L;

        Mockito.when(citizenApisHolderMock.getBrokerApi(accessToken)).thenReturn(brokerApiMock);
        Mockito.when(brokerApiMock.getBrokerInfo(brokerId))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "NotFound", null, null, null));

        BrokerInfoDTO response = brokerClient.getBrokerInfo(brokerId,accessToken);

        Assertions.assertNull(response);
    }
}