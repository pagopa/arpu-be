package it.gov.pagopa.arc.connector.googlerecaptcha.config;

import it.gov.pagopa.arc.connector.BaseApiHolderTest;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.config.GoogleRecaptchaApiClientConfig;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.config.GoogleRecaptchaApisHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleRecaptchaApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private GoogleRecaptchaApisHolder googleRecaptchaApisHolder;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        GoogleRecaptchaApiClientConfig clientConfig = GoogleRecaptchaApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        googleRecaptchaApisHolder = new GoogleRecaptchaApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }


    @Test
    void whenGetOrganizationApiThenAuthenticationShouldBeSetInThreadSafeMode() {
        assertUnauthenticatedApiInvocation(
                () -> googleRecaptchaApisHolder.getGoogleRecaptchaApi()
                        .siteVerify("secret","response","remoteIp"),
                new ParameterizedTypeReference<>() {});
    }
}