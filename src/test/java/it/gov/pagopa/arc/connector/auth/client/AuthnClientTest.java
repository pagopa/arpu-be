package it.gov.pagopa.arc.connector.auth.client;

import it.gov.pagopa.arc.connector.auth.config.AuthApisHolder;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.pu.auth.controller.generated.AuthnApi;
import it.gov.pagopa.pu.auth.dto.generated.AccessToken;
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
class AuthnClientTest {

    @Mock
    private AuthApisHolder authApisHolderMock;
    @Mock
    private AuthnApi authnApiMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    private AuthnClient authnClient;

    @BeforeEach
    void setUp() {
        authnClient = new AuthnClient(authApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                authApisHolderMock
        );
    }

    @Test
    void givenParamsWhenGetTokenThenReturnAccessToken(){
        //given
        String grantType = "grantType";
        String scope = "scope";
        String clientId= "clientId";
        String subjectToken = "subjectToken";
        String subjectIssuer = "subjectIssuer";
        String subjectTokenType = "subjectTokenType";
        String clientSecret = "clientSecret";

        AccessToken expectedResult = podamFactory.manufacturePojo(AccessToken.class);
        Mockito.when(authApisHolderMock.getAuthnApi(null)).thenReturn(authnApiMock);
        Mockito.when(authnApiMock.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret )).thenReturn(expectedResult);

        //when
        AccessToken result = authnClient.postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret);

        //then
        assertNotNull(result);
        assertEquals(expectedResult, result);

    }


}