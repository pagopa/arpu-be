package it.gov.pagopa.arc.connector.auth.service;

import it.gov.pagopa.arc.connector.auth.client.AuthnClient;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.auth.dto.generated.AccessToken;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.co.jemos.podam.api.PodamFactory;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class AuthAccessTokenRetrieverTest {

    @Mock
    private AuthnClient authnClientMock;
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    AuthAccessTokenRetriever authAccessTokenRetriever;

    @BeforeEach
    void setUp() {
        authAccessTokenRetriever = new AuthAccessTokenRetriever("clientSecretMock", authnClientMock);
    }

    @Test
    void givenParamsWhenGetAccessTokenThenReturnAccessToken(){
        AccessToken accessToken = podamFactory.manufacturePojo(AccessToken.class);
        String grantType = "client_credentials";
        String scope = "openid";
        String clientId= "piattaforma-unitaria_";
        String clientSecret = "clientSecretMock";

        Mockito.when(authnClientMock.postToken(clientId, grantType, scope, null, null, null, clientSecret )).thenReturn(accessToken);

        AccessToken result = authAccessTokenRetriever.getAccessToken();

        assertNotNull(result);
        assertSame(accessToken, result);
    }

    @Test
    void givenExistingValidTokenWhenGetAccessTokenThenReturnCachedToken() {
        // given
        AccessToken cachedToken = podamFactory.manufacturePojo(AccessToken.class);
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(10);

        Map<String, Pair<LocalDateTime, AccessToken>> tokenMap =
                (Map<String, Pair<LocalDateTime, AccessToken>>)
                        ReflectionTestUtils.getField(authAccessTokenRetriever, "clientId2accessTokensMap");

        tokenMap.put("piattaforma-unitaria_", Pair.of(expiration, cachedToken));

        // when
        AccessToken result = authAccessTokenRetriever.getAccessToken();

        // then
        assertNotNull(result);
        assertSame(cachedToken, result);
        Mockito.verifyNoInteractions(authnClientMock);
    }
}