package it.gov.pagopa.arc.connector.citizen.config;

import it.gov.pagopa.arc.config.rest.HttpClientErrorJsonBodyHandler;
import it.gov.pagopa.pu.citizen.generated.ApiClient;
import it.gov.pagopa.pu.citizen.generated.BaseApi;
import it.gov.pagopa.pu.citizen.client.generated.*;
import it.gov.pagopa.pu.citizen.dto.generated.ErrorDTO;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

@Service
public class CitizenApisHolder {

    private final OrganizationApi organizationApi;
    private final DebtPositionTypeOrgApi debtPositionTypeOrgApi;
    private final DebtPositionApi debtPositionApi;
    private final ReceiptApi receiptApi;
    private final BrokerApi brokerApi;
    private final InstallmentApi installmentApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    public CitizenApisHolder(
            CitizenApiClientConfig clientConfig,
            RestTemplateBuilder restTemplateBuilder,
            JsonMapper jsonMapper
    ) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setBearerToken(bearerTokenHolder::get);
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        restTemplate.setErrorHandler(new HttpClientErrorJsonBodyHandler<>(jsonMapper, "PU-CITIZEN", clientConfig.isPrintBodyWhenError(),
                ErrorDTO.class, ErrorDTO::getCode, ErrorDTO::getMessage)
        );

        this.organizationApi = new OrganizationApi(apiClient);
        this.debtPositionTypeOrgApi = new DebtPositionTypeOrgApi(apiClient);
        this.debtPositionApi = new DebtPositionApi(apiClient);
        this.receiptApi = new ReceiptApi(apiClient);
        this.brokerApi = new BrokerApi(apiClient);
        this.installmentApi = new InstallmentApi(apiClient);
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

    /** It will return a {@link DebtPositionApi} instrumented with the provided accessToken. Use null if auth is not required */
    public DebtPositionApi getDebtPositionApi(String accessToken){
        return getApi(accessToken, debtPositionApi);
    }

    /** It will return a {@link ReceiptApi} instrumented with the provided accessToken. Use null if auth is not required */
    public ReceiptApi getReceiptApi(String accessToken){
        return getApi(accessToken, receiptApi);
    }

    /** It will return a {@link BrokerApi} instrumented with the provided accessToken. Use null if auth is not required */
    public BrokerApi getBrokerApi(String accessToken){
        return getApi(accessToken, brokerApi);
    }

    /** It will return a {@link InstallmentApi} instrumented with the provided accessToken. Use null if auth is not required */
    public InstallmentApi getInstallmentApi(String accessToken){
        return getApi(accessToken, installmentApi);
    }

    private <T extends BaseApi> T getApi(String accessToken, T api) {
        bearerTokenHolder.set(accessToken);
        return api;
    }
}
