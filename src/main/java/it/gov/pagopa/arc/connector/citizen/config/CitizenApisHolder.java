package it.gov.pagopa.arc.connector.citizen.config;

import it.gov.pagopa.arc.config.rest.RestTemplateConfig;

import it.gov.pagopa.pu.citizen.controller.ApiClient;
import it.gov.pagopa.pu.citizen.controller.BaseApi;
import it.gov.pagopa.pu.citizen.controller.generated.DebtPositionTypeOrgApi;
import it.gov.pagopa.pu.citizen.controller.generated.OrganizationApi;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CitizenApisHolder {

    private final OrganizationApi organizationApi;
    private final DebtPositionTypeOrgApi debtPositionTypeOrgApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public CitizenApisHolder(CitizenApiClientConfig clientConfig, RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("CITIZEN"));
        }

        this.organizationApi = new OrganizationApi(apiClient);
        this.debtPositionTypeOrgApi = new DebtPositionTypeOrgApi(apiClient);
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link OrganizationApi} instrumented with the provided accessToken. Use null if auth is not required */
    public OrganizationApi getOrganizationApi(String accessToken){
        return getApi(accessToken, organizationApi);
    }

    /** It will return a {@link DebtPositionTypeOrgApi} instrumented with the provided accessToken. Use null if auth is not required */
    public DebtPositionTypeOrgApi getDebtPositionTypeOrgApi(String accessToken){
        return getApi(accessToken, debtPositionTypeOrgApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
