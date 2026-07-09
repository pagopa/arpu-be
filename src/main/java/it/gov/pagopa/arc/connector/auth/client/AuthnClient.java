package it.gov.pagopa.arc.connector.auth.client;

import it.gov.pagopa.arc.connector.auth.config.AuthApisHolder;
import it.gov.pagopa.pu.auth.dto.generated.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthnClient {

  private final AuthApisHolder authApisHolder;

  public AuthnClient(AuthApisHolder authApisHolder) {
    this.authApisHolder = authApisHolder;
  }

  public AccessToken postToken(String clientId, String grantType, String scope, String subjectToken, String subjectIssuer, String subjectTokenType, String clientSecret) {
    return authApisHolder.getAuthnApi(null)
            .postToken(clientId, grantType, scope, subjectToken, subjectIssuer, subjectTokenType, clientSecret, null);
  }
}
