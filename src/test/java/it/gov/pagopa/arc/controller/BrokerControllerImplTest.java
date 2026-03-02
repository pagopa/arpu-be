package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.controller.generated.BrokerApi;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.service.broker.BrokerFacadeService;
import it.gov.pagopa.arc.utils.SecurityUtilsTest;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.citizen.dto.generated.BrokerInfoDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BrokerControllerImplTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private BrokerFacadeService brokerFacadeServiceMock;
    private BrokerApi brokerController;
    private final IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

    @BeforeEach
    void setUp() {
        SecurityUtilsTest.configureSecurityContext(loggedUser);
        brokerController = new BrokerControllerImpl(brokerFacadeServiceMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                brokerFacadeServiceMock
        );
        SecurityUtilsTest.clearSecurityContext();
    }

    @AfterEach
    void clearContext() {
        SecurityUtilsTest.clearSecurityContext();
    }

    @Test
    void whenGetPublicBrokerInfoThenOk() {
        Long brokerId = 1L;

        BrokerInfoDTO expectedResult = podamFactory.manufacturePojo(BrokerInfoDTO.class);

        Mockito.when(brokerFacadeServiceMock.getBrokerInfo(brokerId, null))
                .thenReturn(expectedResult);

        ResponseEntity<BrokerInfoDTO> response = brokerController.getPublicBrokerInfo(brokerId, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResult, response.getBody());
    }

    @Test
    void givenNoBrokerWhenGetPublicBrokerInfoThenNotFound() {
        Long brokerId = 1L;

        Mockito.when(brokerFacadeServiceMock.getBrokerInfo(brokerId, null))
                .thenReturn(null);

        ResponseEntity<BrokerInfoDTO> response = brokerController.getPublicBrokerInfo(brokerId, null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}