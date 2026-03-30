package it.gov.pagopa.arc.connector.googlerecaptcha;

import it.gov.pagopa.arc.connector.external.googlerecaptcha.GoogleRecaptchaService;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.GoogleRecaptchaServiceImpl;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.client.GoogleRecaptchaClient;
import it.gov.pagopa.arc.utils.TestUtils;
import it.gov.pagopa.google.recaptcha.dto.generated.SiteVerifyResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GoogleRecaptchaServiceImplTest {
    public static final String GOOGLE_RECAPTCHA_SECRET = "googleRecaptchaSecret";
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private GoogleRecaptchaClient googleRecaptchaClientMock;
    private GoogleRecaptchaService googleRecaptchaService;

    @BeforeEach
    void setUp() {
        googleRecaptchaService = new GoogleRecaptchaServiceImpl(googleRecaptchaClientMock, GOOGLE_RECAPTCHA_SECRET);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                googleRecaptchaClientMock
        );
    }

    @Test
    void givenSuccessTrueWhenSiteVerifyThenTrue() {
        String recaptchaToken = "recaptchaToken";
        SiteVerifyResponseDTO siteVerifyResponseDTO = podamFactory.manufacturePojo(SiteVerifyResponseDTO.class);
        siteVerifyResponseDTO.setSuccess(true);

        Mockito.when(googleRecaptchaClientMock.siteVerify(GOOGLE_RECAPTCHA_SECRET, recaptchaToken, null)).thenReturn(siteVerifyResponseDTO);

        boolean result = googleRecaptchaService.isVerified(recaptchaToken);

        assertTrue(result);
    }

    @Test
    void givenFailureWhenSiteVerifyThenFalse() {
        String recaptchaToken = "recaptchaToken";
        SiteVerifyResponseDTO siteVerifyResponseDTO = podamFactory.manufacturePojo(SiteVerifyResponseDTO.class);
        siteVerifyResponseDTO.setSuccess(false);

        Mockito.when(googleRecaptchaClientMock.siteVerify(GOOGLE_RECAPTCHA_SECRET, recaptchaToken, null)).thenReturn(siteVerifyResponseDTO);

        boolean result = googleRecaptchaService.isVerified(recaptchaToken);

        assertFalse(result);
    }

    @Test
    void givenNullSuccessWhenSiteVerifyThenFalse() {
        String recaptchaToken = "recaptchaToken";
        SiteVerifyResponseDTO siteVerifyResponseDTO = podamFactory.manufacturePojo(SiteVerifyResponseDTO.class);
        siteVerifyResponseDTO.setSuccess(null);

        Mockito.when(googleRecaptchaClientMock.siteVerify(GOOGLE_RECAPTCHA_SECRET, recaptchaToken, null)).thenReturn(siteVerifyResponseDTO);

        boolean result = googleRecaptchaService.isVerified(recaptchaToken);

        assertFalse(result);
    }

    @Test
    void givenNullResponseWhenSiteVerifyThenFalse() {
        String recaptchaToken = "recaptchaToken";

        Mockito.when(googleRecaptchaClientMock.siteVerify(GOOGLE_RECAPTCHA_SECRET, recaptchaToken, null)).thenReturn(null);

        boolean result = googleRecaptchaService.isVerified(recaptchaToken);

        assertFalse(result);
    }
}