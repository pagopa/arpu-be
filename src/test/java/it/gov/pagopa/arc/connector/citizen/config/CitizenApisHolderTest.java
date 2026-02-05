package it.gov.pagopa.arc.connector.citizen.config;

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
                        .getDebtPositionTypeOrgsWithSpontaneous(1L,1L),
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

    @Test
    void whenGetReceiptApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> citizenApisHolder.getReceiptApi(accessToken)
                        .getPagedDebtorReceipts(1L,"fiscalCode","orgName",
                        "noticeNumberOrIuv", OffsetDateTime.now(),
                        OffsetDateTime.now(), 0, 1, new ArrayList<>()),
                new ParameterizedTypeReference<>() {},
                citizenApisHolder::unload);
    }

    @Test
    void whenGetBrokerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> citizenApisHolder.getBrokerApi(accessToken)
                        .getBrokerInfo(1L),
                new ParameterizedTypeReference<>() {},
                citizenApisHolder::unload);
    }

    @Test
    void whenGetInstallmentApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> citizenApisHolder.getInstallmentApi(accessToken)
                        .getInstallmentsByIuvOrNav(1L,"iuvOrNav","debtorFiscalCode","orgFiscalCode", List.of(InstallmentStatus.PAID)),
                new ParameterizedTypeReference<>() {},
                citizenApisHolder::unload);
    }
}