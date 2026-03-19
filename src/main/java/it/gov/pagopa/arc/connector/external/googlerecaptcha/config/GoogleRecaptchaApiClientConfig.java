package it.gov.pagopa.arc.connector.external.googlerecaptcha.config;

import it.gov.pagopa.arc.config.rest.ApiClientConfig;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rest-client.google-recaptcha")
@SuperBuilder
@NoArgsConstructor
public class GoogleRecaptchaApiClientConfig extends ApiClientConfig {
}
