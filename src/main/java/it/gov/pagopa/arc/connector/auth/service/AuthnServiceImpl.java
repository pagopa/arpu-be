package it.gov.pagopa.arc.connector.auth.service;


import org.springframework.stereotype.Service;

@Service
public class AuthnServiceImpl implements AuthnService {

    private final AuthAccessTokenRetriever accessTokenRetriever;

    public AuthnServiceImpl(AuthAccessTokenRetriever accessTokenRetriever) {
        this.accessTokenRetriever = accessTokenRetriever;
    }

    @Override
    public String getAccessToken() {
        return accessTokenRetriever.getAccessToken()
                .getAccessToken();
    }

}
