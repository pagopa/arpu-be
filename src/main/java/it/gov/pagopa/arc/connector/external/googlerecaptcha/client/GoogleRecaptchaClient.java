package it.gov.pagopa.arc.connector.external.googlerecaptcha.client;


import it.gov.pagopa.arc.connector.external.googlerecaptcha.config.GoogleRecaptchaApisHolder;
import it.gov.pagopa.google.recaptcha.dto.generated.SiteVerifyResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GoogleRecaptchaClient {
    private final GoogleRecaptchaApisHolder apisHolder;

    public GoogleRecaptchaClient(GoogleRecaptchaApisHolder apisHolder) {
        this.apisHolder = apisHolder;
    }

    public SiteVerifyResponseDTO siteVerify(String secret, String recaptchaToken, String remoteIp){
        return apisHolder.getGoogleRecaptchaApi().siteVerify(secret, recaptchaToken, remoteIp);
    }
}
