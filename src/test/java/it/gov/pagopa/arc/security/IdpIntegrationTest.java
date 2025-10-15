package it.gov.pagopa.arc.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import it.gov.pagopa.arc.config.WireMockConfig;
import it.gov.pagopa.arc.connector.auth.client.AuthnClient;
import it.gov.pagopa.arc.connector.auth.config.AuthApisHolder;
import it.gov.pagopa.arc.connector.auth.service.AuthnService;
import it.gov.pagopa.arc.model.generated.TokenResponse;
import it.gov.pagopa.arc.model.generated.UserInfo;
import it.gov.pagopa.arc.service.TokenStoreService;
import it.gov.pagopa.arc.utils.CertUtils;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static it.gov.pagopa.arc.config.WireMockConfig.WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration( initializers = WireMockConfig.WireMockInitializer.class )
@TestPropertySource(
    properties = {
        "spring.security.oauth2.client.registration.oneidentity.provider=oneidentity",
        "spring.security.oauth2.client.registration.oneidentity.client-id=clientid",
        "spring.security.oauth2.client.registration.oneidentity.client-secret=secret",
        "spring.security.oauth2.client.registration.oneidentity.authorization-grant-type=authorization_code",
        "spring.security.oauth2.client.registration.oneidentity.redirect-uri=http://idp/callback",
        "spring.security.oauth2.client.registration.oneidentity.scope=openid",
        "spring.security.oauth2.client.provider.oneidentity.authorization-uri=http://idp/login",
        "spring.security.oauth2.client.provider.oneidentity.user-name-attribute=sub",

        "jwt.audience=application",
        "jwt.tokenType=Bearer",
        "jwt.access-token.expire-in=3600",
        "jwt.access-token.private-key=-----BEGIN PRIVATE KEY----- MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCYGJCFUvgUr20I /H2MLqtFjKX8IZ6VbxiNZD/I/U2zWhYnGffi6QAeecB8RIZf3rt0fdwZ2l6UZ914 mTnsGfE6yk1ee4wmRovUYpDq2O/lApU1TFPaLx0OOSH3kxFJhFnYaQtmBBUDsBrs PSjj9qzq4R0WcUAs0lYE3CJ8BhDA5U0x2EWSeXM9BySL79xZ0g/XhpDs4ngjXfTT o9RTUf3tfuhECTYa8LCddXUqGiBwDACK9vISWm12JKeV03Mp4/WQxX0iB52KSQBV UTHul3msZx1z6s1FcbgtiQnQgDGXMNwWcfJpZFaedTRL3ZDnSghgko1BI0guTmVj wJlYPF4bAgMBAAECggEAA91/8rtwjYoFwdg00pavCJXx8+3gy1hm7dTx4Ag77MZp 0LWSvKQCOkQK1b2iEpak+elm6gtIIwpesP1n4O2p2T4h6DhIkAJz9EJK/4Ti19WQ eCnH6cAPw3hFOjb1FgK0i9DjlsScyhq0HHPTcbOnolJ1PEhFgr4XrIjxoWhADb7b /pdTJBHwNcBAFtz3LPJNR1d5sE0lGz+P7OwP/jqoDK8LtQqS0Yl1eIOiK/d7zD8H ZV928xt3/K4xapif1FWU7mAkpI0USjoeXJF4ZHP8BhhuBnOwswl6QEZ+S+l3daDd Avcr5Cv97qh9Rolz3ncqH8KMjuFfGBIBAwOYFo9wDQKBgQDWTNRuHcKXSlK7Rar/ aVDs1kvExT3BNf2oaqEI+RQUlhDkbDuWoo093J/xaJHNSCRjLFWVLSIftXVnbCDi HD2XmCHRExpWGw4gZDVNBcLgZh8UWgKlLWYz3Q2etLgG1jNgor/A+86d4WPh+aQX zqtozHXlSecaXywyavB1bTlfbQKBgQC1sRbxS4Ir2M+kW4yPyrIihYhqRwZ8nJOv 3IYmiHwex4t3fpwS4z/dH4B8XNSF68E+KbMRB5YNBNOCwCvGx82s6Bq3P8WR74NV k4bhdn2uPku0u+Gteij8A4ONyL1GBbmFn3l/OmsNQTD+2prcexBuGKh7K7yus66F gph7gfLWpwKBgF1xuPufLHfN589TLKIcqTXsp7NQkoIKaeYjQL7p5XCokwsXitA/ Zzk/V9rrTxBlUcCQ12yp9oQ/GseTJa+SwuS0aKKDIuvC9mD3cSp5xaUVwp2cNiUS a8tXq5W1lb0db9/Gd7jN1CWR33zs3zmmW6Xh6dKmbAha0anWaa26h9btAoGAX/N/ vDo2KlW7gn7egml3HYgLfKS5pkFCNVNufRcDBXY4DwkL/2WHqo0iW4riqT7RtLRs 3od1FLcBxEEcXUPTOIby5OeGvQUSBLV+O79JrCU18eJu0iB7WGu6o7vpSPto+Eo5 7Zi6RCuzZkOoGNvc12eqQjHc2R4HAnbvc/oydm0CgYEAwCoHnT4wa/I1I1Few/C0 v/T8EIikZv/ahjAtBDyCS/ECPWHT/rNoVACli8qRODc5QsYnbfusKruGdjYFYZrK DoQdpTUjoj8IA7SrwiifgPvTo+DAv89Weg6GirhhjvG+9s5rtDsrDMNWpthfs0g5 erTxidoSCP2fjk5BpdnE+Ww= -----END PRIVATE KEY-----",
        "jwt.access-token.public-key=-----BEGIN PUBLIC KEY----- MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmBiQhVL4FK9tCPx9jC6r RYyl/CGelW8YjWQ/yP1Ns1oWJxn34ukAHnnAfESGX967dH3cGdpelGfdeJk57Bnx OspNXnuMJkaL1GKQ6tjv5QKVNUxT2i8dDjkh95MRSYRZ2GkLZgQVA7Aa7D0o4/as 6uEdFnFALNJWBNwifAYQwOVNMdhFknlzPQcki+/cWdIP14aQ7OJ4I13006PUU1H9 7X7oRAk2GvCwnXV1KhogcAwAivbyElptdiSnldNzKeP1kMV9IgedikkAVVEx7pd5 rGcdc+rNRXG4LYkJ0IAxlzDcFnHyaWRWnnU0S92Q50oIYJKNQSNILk5lY8CZWDxe GwIDAQAB -----END PUBLIC KEY-----",

        WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX+"spring.security.oauth2.client.provider.oneidentity.token-uri=idp/oidc/token",
        WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX+"spring.security.oauth2.client.provider.oneidentity.jwk-set-uri=idp/oidc/keys",
        WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX + "spring.security.oauth2.client.provider.oneidentity.issuer-uri=idp",

        "spring.application.name=app",
        "spring.application.version=1",
        "rest-client.pull-payment.baseUrl=pullPaymentMock",
        "rest-client.pull-payment.api-key=x_api_key0",
        "rest-client.biz-events.paid-notice.baseUrl= bizEventsMock",
        "rest-client.biz-events.paid-notice.api-key=x_api_key0",
        "rest-client.gpd.baseUrl=gpdMock",
        "rest-client.gpd.api-key=x_api_key0",
        "white-list-cf-users=PLOMRC01P30L736Y",
        "spring.cache.type=simple",
        "spontaneous-mock-paths.organizationList=mock/organizationsMock.json"
    })
@AutoConfigureMockMvc
class IdpIntegrationTest {
    private static final String LOGIN_URL = "/login/oneidentity";
    private static final String TOKEN_URL = "/token/oneidentity";
    private static final String IDP_KEYS_URL = "/idp/oidc/keys";
    private static final String IDP_TOKEN_URL = "/idp/oidc/token";

    private static final String USER_INFO_URL = "/auth/user";

    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private Environment environment;
    @Autowired
    private TokenStoreService tokenStoreService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    AuthnClient authnClient;
    @MockitoBean
    AuthnService authnService;
    @MockitoBean
    AuthApisHolder authApisHolder;

    private static RSAPublicKey rsaPublicKey = null;
    private static RSAPrivateKey rsaPrivateKey = null;
    private static String modulusBase64 = null;

    @BeforeAll
    static void setup() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        modulusBase64 = Base64.getEncoder().encodeToString(rsaPublicKey.getModulus().toByteArray());
    }
    @Test
    void givenLoginActionThenGetNewState() throws Exception {

        MvcResult result = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = extractQueryParams(result);

        Assertions.assertNotNull(queryParams.get("nonce").getFirst());
        Assertions.assertNotNull(queryParams.get("state").getFirst());
        Assertions.assertEquals(environment.getProperty("spring.security.oauth2.client.registration.oneidentity.client-id"),queryParams.get("client_id").getFirst());
        Assertions.assertEquals(environment.getProperty("spring.security.oauth2.client.registration.oneidentity.scope"),queryParams.get("scope").getFirst());
        Assertions.assertEquals(environment.getProperty("spring.security.oauth2.client.registration.oneidentity.redirect-uri"),queryParams.get("redirect_uri").getFirst());

    }

    @Test
    void givenValidStateThenRequestAccessToken() throws Exception {
        addStubKeys(modulusBase64);

        MvcResult result = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = extractQueryParams(result);
        String idpIdToken = genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey);

        addStubToken(idpIdToken);

        MvcResult firstTimeToken = mockMvc.perform(get(TOKEN_URL)
                .param("code","code")
                .param("state", decodeState(queryParams.get("state").getFirst()) ))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        TokenResponse token = objectMapper.readValue(firstTimeToken.getResponse().getContentAsString(),TokenResponse.class);

        Assertions.assertNotNull(firstTimeToken);
        Assertions.assertNotNull(token);
        Assertions.assertNotNull(token.getAccessToken());
        Assertions.assertNotNull(token.getTokenType());
        Assertions.assertNotNull(token.getExpiresIn());
    }

    @Test
    void givenInvalidStateThenRequestAccessToken() throws Exception {
        addStubKeys(modulusBase64);

        MvcResult firstTimeToken = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = UriComponentsBuilder.newInstance().
            query(firstTimeToken.getResponse().getRedirectedUrl()).build().getQueryParams();
        String idpIdToken = genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey);

        addStubToken(idpIdToken);

        MvcResult secondTimeToken = mockMvc.perform(get(TOKEN_URL)
                .param("code","code")
                .param("state", UUID.randomUUID().toString() ))
            .andReturn();

        Assertions.assertNotNull(secondTimeToken);
        Assertions.assertEquals(400, secondTimeToken.getResponse().getStatus());

    }


    @Test
    void givenAlreadyUsedStateThenRequestAccessToken() throws Exception {
        addStubKeys(modulusBase64);

        MvcResult result = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = extractQueryParams(result);
        String idpIdToken = genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey);

        addStubToken(idpIdToken);

        MvcResult firstTimeToken = mockMvc.perform(get(TOKEN_URL)
                .param("code","code")
                .param("state", decodeState(queryParams.get("state").getFirst()) ))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        TokenResponse token = objectMapper.readValue(firstTimeToken.getResponse().getContentAsString(),TokenResponse.class);

        // Expected error cause state has already been used
        MvcResult secondTimeToken = mockMvc.perform(get("/token/oneidentity")
                .param("code","code")
                .param("state", decodeState(queryParams.get("state").getFirst()) ))
            .andReturn();

        Assertions.assertNotNull(token);
        Assertions.assertNotNull(firstTimeToken);
        Assertions.assertNotNull(secondTimeToken);
        Assertions.assertEquals(400, secondTimeToken.getResponse().getStatus());
    }

    @Test
    void givenAnEmptyStateThenRequestAccessToken() throws Exception {
        addStubKeys(modulusBase64);

        MvcResult result = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = extractQueryParams(result);
        String idpIdToken = genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey);

        addStubToken(idpIdToken);

        MvcResult tokenResult = mockMvc.perform(get(TOKEN_URL)
                .param("code","code"))
            .andExpect(status().is(400))
            .andReturn();
        Assertions.assertNotNull(tokenResult);
    }

    @Test
    void givenValidAccessTokenThenGetUserInfo() throws Exception {
        addStubKeys(modulusBase64);

        MvcResult result = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = extractQueryParams(result);
        String idpIdToken = genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey);

        addStubToken(idpIdToken);

        MvcResult firstTimeToken = mockMvc.perform(get(TOKEN_URL)
                .param("code","code")
                .param("state", decodeState(queryParams.get("state").getFirst()) ))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        TokenResponse token = objectMapper.readValue(firstTimeToken.getResponse().getContentAsString(),TokenResponse.class);

        MvcResult userResp = mockMvc.perform(get(USER_INFO_URL)
                .header("Authorization","Bearer "+token.getAccessToken()))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        UserInfo user = objectMapper.readValue(userResp.getResponse().getContentAsString(),UserInfo.class);
        Assertions.assertNotNull(userResp);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getName());
        Assertions.assertNotNull(user.getFamilyName());
        Assertions.assertNotNull(user.getEmail());
        Assertions.assertNotNull(user.getFiscalCode());
        Assertions.assertNotNull(user.getUserId());
    }

    @Test
    void givenInvalidAccessTokenThenGetUserInfo() throws Exception {
        addStubKeys(modulusBase64);

        MvcResult result = mockMvc.perform(get(LOGIN_URL))
            .andExpect(status().is3xxRedirection())
            .andReturn();

        MultiValueMap<String,String> queryParams = extractQueryParams(result);
        String idpIdToken = genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey);

        addStubToken(idpIdToken);

        mockMvc.perform(get(TOKEN_URL)
                .param("code","code")
                .param("state", decodeState(queryParams.get("state").getFirst()) ))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        MvcResult userResp = mockMvc.perform(get(USER_INFO_URL)
                .header("Authorization","Bearer "+genIdpIdToken(queryParams,rsaPublicKey,rsaPrivateKey)))
            .andExpect(status().is(401))
            .andReturn();

        Assertions.assertNotNull(userResp);
    }

    @Test
    void givenExpiredAccessTokenThenGetUserInfo() throws Exception {

        String token = TestUtils.genToken(
            decodePublicKey(environment.getProperty("jwt.access-token.public-key")),
            decodePrivateKey(environment.getProperty("jwt.access-token.private-key")),
            -1,
            "application");
        MvcResult userResp = mockMvc.perform(get(USER_INFO_URL)
                .header("Authorization","Bearer "+token))
            .andExpect(status().is(401))
            .andReturn();

        Assertions.assertNotNull(userResp);
    }

    @Test
    void givenInvalidSignatureThenGetUserInfo() throws Exception {
        KeyPair kp = TestUtils.genKeyPair();
        String token = TestUtils.genToken(
            (RSAPublicKey) kp.getPublic(),
            (RSAPrivateKey) kp.getPrivate(),
            -1,
            "application");
        MvcResult userResp = mockMvc.perform(get(USER_INFO_URL)
                .header("Authorization","Bearer "+token))
            .andExpect(status().is(401))
            .andReturn();

        Assertions.assertNotNull(userResp);
    }

    private String genIdpIdToken(MultiValueMap<String,String> m,RSAPublicKey publicKey,RSAPrivateKey privateKey){
        return JWT.create()
            .withClaim("typ","Bearer" )
            .withClaim("sub","_7284fdec21b65e716223feeb9b3564c1")
            .withClaim("familyName","Polo")
            .withClaim("name","Marco")
            .withClaim("fiscalNumber","TINIT-PLOMRC01P30L736Y")
            .withClaim("email","ilmilione@virgilio.it")
            .withClaim("aud","clientid")
            .withClaim("nonce",m.get("nonce").getFirst())
            .withIssuer(wireMockServer.baseUrl()+"/idp")
            .withJWTId(UUID.randomUUID().toString())
            .withIssuedAt(Instant.now())
            .withExpiresAt(Instant.now().plusSeconds(3600))
            .sign(Algorithm.RSA256(publicKey, privateKey));
    }
    private void addStubKeys(String modulusBase64) {
        wireMockServer.stubFor(WireMock.get(IDP_KEYS_URL)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"keys\": [{\"kty\": \"RSA\", \"kid\": \"ce617dc9-83a9-4a4e-b060-2cdf9575f05a\", \"use\": \"sig\", \"alg\": \"RS256\", \"n\": \"" + modulusBase64 + "\", \"e\": \"AQAB\"}]}")));
    }
    private void addStubToken(String token) {
        wireMockServer.stubFor(post(IDP_TOKEN_URL)
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("{\"access_token\": \"Aas6VVQNsQItCgCo6n_R_Cblfj5QBeNj80IlAMYz5gY\", \"token_type\": \"Bearer\", \"expires_in\": 900, \"id_token\": \"" + token + "\"}")));
    }

    private MultiValueMap<String, String> extractQueryParams(MvcResult result) {
        String location = result.getResponse().getHeader("Location");
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(location).build();
        return uriComponents.getQueryParams();
    }

    private String decodeState(String state) throws UnsupportedEncodingException {
        return java.net.URLDecoder.decode(state, StandardCharsets.UTF_8.name());
    }

    public static RSAPublicKey decodePublicKey(String publicKeyPEM)
        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return CertUtils.pemPub2PublicKey(publicKeyPEM);
    }

    public static RSAPrivateKey decodePrivateKey(String privateKeyPEM)
        throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        return CertUtils.pemKey2PrivateKey(privateKeyPEM);
    }

}
