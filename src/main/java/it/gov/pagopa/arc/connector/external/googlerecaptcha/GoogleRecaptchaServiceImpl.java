package it.gov.pagopa.arc.connector.external.googlerecaptcha;

import it.gov.pagopa.arc.connector.external.googlerecaptcha.client.GoogleRecaptchaClient;
import it.gov.pagopa.google.recaptcha.dto.generated.SiteVerifyResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleRecaptchaServiceImpl implements GoogleRecaptchaService {
    private final GoogleRecaptchaClient googleRecaptchaClient;
    private final String googleRecaptchaSecret;

    public GoogleRecaptchaServiceImpl(GoogleRecaptchaClient googleRecaptchaClient, @Value("${rest-client.google-recaptcha.recaptcha.secret}") String googleRecaptchaSecret) {
        this.googleRecaptchaClient = googleRecaptchaClient;
        this.googleRecaptchaSecret = googleRecaptchaSecret;
    }

    @Override
    public boolean isVerified(String recaptchaToken) {
        SiteVerifyResponseDTO siteVerifyResponseDTO = googleRecaptchaClient.siteVerify(googleRecaptchaSecret, recaptchaToken, null);
        return siteVerifyResponseDTO != null &&
                Boolean.TRUE.equals(siteVerifyResponseDTO.getSuccess());
    }
}
