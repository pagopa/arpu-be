package it.gov.pagopa.arc.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.arc.config.JWTConfiguration;
import it.gov.pagopa.arc.config.JWTSampleConfiguration;
import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.model.generated.TokenResponse;
import it.gov.pagopa.arc.service.AccessTokenBuilderService;
import it.gov.pagopa.arc.service.TokenStoreService;
import it.gov.pagopa.arc.service.TokenStoreServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import tools.jackson.databind.json.JsonMapper;
import wiremock.org.apache.hc.core5.http.ContentType;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

class CustomAuthenticationSuccessHandlerTest {

  private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

  @Mock
  private TokenStoreService tokenStoreService;

  @BeforeEach
  void setUp(){
    tokenStoreService = mock(TokenStoreServiceImpl.class);
    JWTConfiguration jwtConfiguration = JWTSampleConfiguration.getCorrectConfiguration();
    customAuthenticationSuccessHandler = new CustomAuthenticationSuccessHandler(
        new AccessTokenBuilderService(jwtConfiguration),
        new JsonMapper(),
        jwtConfiguration,
        tokenStoreService,
        Set.of("PLOMRC01P30L736Y"));
  }
  @Test
  void givenAuthenticationRequestThenInResponseGetCustomTokenResponse() throws IOException {
    OAuth2AuthenticationToken oAuth2AuthenticationToken = getAuthenticationToken(true);
    Mockito.when(tokenStoreService.getUserInfo(anyString())).thenReturn(new IamUserInfoDTO());

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();

    customAuthenticationSuccessHandler.onAuthenticationSuccess(request,response,oAuth2AuthenticationToken);

    TokenResponse token = new ObjectMapper().readValue(response.getContentAsString(),TokenResponse.class);

    assertEquals(ContentType.APPLICATION_JSON.getMimeType(),response.getContentType());
    assertNotNull(tokenStoreService.getUserInfo(token.getAccessToken()));
    assertEquals("Bearer",token.getTokenType());
    assertEquals(3600,token.getExpiresIn());

    assertNotNull(JWT.decode(token.getAccessToken()));
  }

  @Test
  void givenAuthenticationRequestThenInResponseGetForbidden()
      throws IOException {
    OAuth2AuthenticationToken oAuth2AuthenticationToken = getAuthenticationToken(false);

    MockHttpServletResponse response = new MockHttpServletResponse();
    MockHttpServletRequest request = new MockHttpServletRequest();

    customAuthenticationSuccessHandler.onAuthenticationSuccess(request,response,oAuth2AuthenticationToken);

    assertEquals(ContentType.APPLICATION_JSON.getMimeType(), response.getContentType());
    assertEquals(403,response.getStatus());
    assertEquals("{\"error\": \"User not allowed to access this application\"}",response.getContentAsString());
  }

  private OAuth2AuthenticationToken getAuthenticationToken(boolean isValidUser){
    Consumer<Map<String, Object>> attributesConsumer = attributes -> {
      attributes.putAll(Map.of(
          "sub", "123456",
          "fiscalNumber", isValidUser ? "TINIT-PLOMRC01P30L736Y" : "TINIT-PLOMRC01P30L736A",
          "familyName", "Polo",
          "name", "Marco",
          "email", "marco.polo@example.com",
          "iss", "issuer"
      ));
    };
    Instant issuedAt = Instant.now();
    Instant expiresAt = issuedAt.plusSeconds(3600);

    OidcIdToken idToken = OidcIdToken.withTokenValue(JWT.create().toString())
        .issuedAt(issuedAt)
        .expiresAt(expiresAt)
        .claims(attributesConsumer)
        .build();
    OAuth2User principal = new DefaultOidcUser(null,idToken);
    return new OAuth2AuthenticationToken(principal,null,"test-client");
  }

}