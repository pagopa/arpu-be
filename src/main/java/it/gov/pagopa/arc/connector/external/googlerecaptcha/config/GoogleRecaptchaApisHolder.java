package it.gov.pagopa.arc.connector.external.googlerecaptcha.config;

import it.gov.pagopa.arc.config.rest.RestTemplateConfig;
import it.gov.pagopa.google.recaptcha.controller.ApiClient;
import it.gov.pagopa.google.recaptcha.controller.BaseApi;
import it.gov.pagopa.google.recaptcha.controller.generated.GoogleRecaptchaApi;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleRecaptchaApisHolder {

    private final GoogleRecaptchaApi googleRecaptchaApi;

    public GoogleRecaptchaApisHolder(GoogleRecaptchaApiClientConfig clientConfig, RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(clientConfig.getBaseUrl());
        apiClient.setMaxAttemptsForRetry(Math.max(1, clientConfig.getMaxAttempts()));
        apiClient.setWaitTimeMillis(clientConfig.getWaitTimeMillis());
        if (clientConfig.isPrintBodyWhenError()) {
            restTemplate.setErrorHandler(RestTemplateConfig.bodyPrinterWhenError("GOOGLE-RECAPTCHA"));
        }

        this.googleRecaptchaApi = new GoogleRecaptchaApi(apiClient);
    }

    /** It will return a {@link GoogleRecaptchaApi} */
    public GoogleRecaptchaApi getGoogleRecaptchaApi(){
        return getApi(googleRecaptchaApi);
    }

    private <T extends BaseApi> T getApi(T api) {
        return api;
    }
}
