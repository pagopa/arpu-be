package it.gov.pagopa.arc.connector.external.googlerecaptcha;

public interface GoogleRecaptchaService {
    boolean isVerified(String recaptchaToken);
}
