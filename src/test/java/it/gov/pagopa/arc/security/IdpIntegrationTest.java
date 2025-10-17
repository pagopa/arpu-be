package it.gov.pagopa.arc.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import it.gov.pagopa.arc.config.WireMockConfig;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
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
@ActiveProfiles("oauth")
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
