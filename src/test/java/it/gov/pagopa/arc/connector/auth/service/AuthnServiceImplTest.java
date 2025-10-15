package it.gov.pagopa.arc.connector.auth.service;

import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.auth.dto.generated.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthnServiceImplTest {
    
    @Mock
    private AuthAccessTokenRetriever accessTokenRetrieverMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private AuthnService authnService;

    @BeforeEach
    void setUp() {
        authnService = new AuthnServiceImpl(accessTokenRetrieverMock);
    }

    @Test
    void whenGetAccessTokenThenReturnAccessToken() {
        //given
        AccessToken accessToken = podamFactory.manufacturePojo(AccessToken.class);
        Mockito.when(accessTokenRetrieverMock.getAccessToken()).thenReturn(accessToken);
        //when
        String result = authnService.getAccessToken();
        //then
        assertNotNull(result);
        assertEquals(accessToken.getAccessToken(), result);
    }
}