package it.gov.pagopa.arc.connector.citizen.config;

import it.gov.pagopa.arc.config.json.JsonConfig;
import it.gov.pagopa.arc.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.citizen.dto.generated.InstallmentStatus;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CitizenApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private CitizenApisHolder apisHolder;
    private CitizenApiClientConfig apiClientConfig;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = CitizenApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        apisHolder = new CitizenApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getOrganizationApi(null));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void testRetryConfiguration() {
        assertRetry(apiClientConfig,
                accessToken -> apisHolder.getOrganizationApi(accessToken)
                        .getOrganizationsWithSpontaneous(1L),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetOrganizationApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getOrganizationApi(accessToken)
                        .getOrganizationsWithSpontaneous(1L),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionTypeOrgApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getDebtPositionTypeOrgApi(accessToken)
                        .getDebtPositionTypeOrgsWithSpontaneous(1L, 1L),
                new ParameterizedTypeReference<>() {
                },
                apisHolder::unload);
    }

    @Test
    void whenGetDebtPositionApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getDebtPositionApi(accessToken)
                        .getUnpaidPaymentNoticeZip(1L, "fiscalCode", 1L),
                new ParameterizedTypeReference<>() {
                },
                apisHolder::unload);
    }

    @Test
    void whenGetReceiptApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getReceiptApi(accessToken)
                        .getPagedDebtorReceipts(1L, "fiscalCode", "orgName",
                                "noticeNumberOrIuv", OffsetDateTime.now(),
                                OffsetDateTime.now(), 0, 1, new ArrayList<>()),
                new ParameterizedTypeReference<>() {
                },
                apisHolder::unload);
    }

    @Test
    void whenGetBrokerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getBrokerApi(accessToken)
                        .getBrokerInfo(1L, "externalId"),
                new ParameterizedTypeReference<>() {
                },
                apisHolder::unload);
    }

    @Test
    void whenGetInstallmentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getInstallmentApi(accessToken)
                        .getInstallmentsByIuvOrNav(1L, "iuvOrNav", "debtorFiscalCode", "orgFiscalCode", List.of(InstallmentStatus.PAID)),
                new ParameterizedTypeReference<>() {
                },
                apisHolder::unload);
    }
}