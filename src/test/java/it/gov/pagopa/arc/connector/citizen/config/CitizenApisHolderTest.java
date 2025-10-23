package it.gov.pagopa.arc.connector.citizen.config;

import it.gov.pagopa.arc.connector.BaseApiHolderTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

@ExtendWith(MockitoExtension.class)
class CitizenApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private CitizenApisHolder citizenApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        CitizenApiClientConfig clientConfig = CitizenApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        citizenApisHolder = new CitizenApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }


    @Test
    void whenGetOrganizationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> citizenApisHolder.getOrganizationApi(accessToken)
                        .getOrganizationsWithSpontaneous(1L),
                new ParameterizedTypeReference<>() {},
                citizenApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> citizenApisHolder.getDebtPositionTypeOrgApi(accessToken)
                        .getDebtPositionTypeOrgsWithSpontaneous(1L),
                new ParameterizedTypeReference<>() {},
                citizenApisHolder::unload);
    }

    @Test
    void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> citizenApisHolder.getDebtPositionApi(accessToken)
                        .getUnpaidPaymentNoticeZip(1L,"fiscalCode",1L),
                new ParameterizedTypeReference<>() {},
                citizenApisHolder::unload);
    }
}