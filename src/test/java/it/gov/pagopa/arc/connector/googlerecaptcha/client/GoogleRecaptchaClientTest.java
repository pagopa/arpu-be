package it.gov.pagopa.arc.connector.googlerecaptcha.client;

import it.gov.pagopa.arc.connector.external.googlerecaptcha.client.GoogleRecaptchaClient;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.config.GoogleRecaptchaApisHolder;
import it.gov.pagopa.google.recaptcha.controller.generated.GoogleRecaptchaApi;
import it.gov.pagopa.google.recaptcha.dto.generated.SiteVerifyResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoogleRecaptchaClientTest {
    @Mock
    private GoogleRecaptchaApisHolder googleRecaptchaApisHolderMock;
    @Mock
    private GoogleRecaptchaApi googleRecaptchaApiMock;
    private GoogleRecaptchaClient googleRecaptchaClient;

    @BeforeEach
    void setUp() {
        googleRecaptchaClient = new GoogleRecaptchaClient(googleRecaptchaApisHolderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                googleRecaptchaApisHolderMock,
                googleRecaptchaApiMock
        );
    }

    @Test
    void whenGetBrokerInfoThenOk(){
        String secret = "secret";
        String recaptchaToken = "recaptchaToken";
        String remoteIp = "remoteIp";
        SiteVerifyResponseDTO expectedResponse = new SiteVerifyResponseDTO();

        Mockito.when(googleRecaptchaApisHolderMock.getGoogleRecaptchaApi()).thenReturn(googleRecaptchaApiMock);
        Mockito.when(googleRecaptchaApiMock.siteVerify(secret,recaptchaToken,remoteIp)).thenReturn(expectedResponse);

        SiteVerifyResponseDTO response = googleRecaptchaClient.siteVerify(secret, recaptchaToken, remoteIp);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(expectedResponse,response);
    }
}