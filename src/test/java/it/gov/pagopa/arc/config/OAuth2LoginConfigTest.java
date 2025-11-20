package it.gov.pagopa.arc.config;

import it.gov.pagopa.arc.controller.generated.ArcAuthApi;
import it.gov.pagopa.arc.controller.generated.ArcZendeskAssistanceApi;
import it.gov.pagopa.arc.controller.generated.OrganizationApi;
import it.gov.pagopa.arc.security.CustomAuthenticationSuccessHandler;
import it.gov.pagopa.arc.security.CustomLogoutHandler;
import it.gov.pagopa.arc.security.CustomLogoutSuccessHandler;
import it.gov.pagopa.arc.service.AccessTokenValidationService;
import it.gov.pagopa.arc.service.AuthService;
import it.gov.pagopa.arc.service.TokenStoreService;
import it.gov.pagopa.arc.service.ZendeskAssistanceTokenService;
import it.gov.pagopa.arc.service.organization.OrganizationFacadeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("oauth")
@WebMvcTest(value = {ArcAuthApi.class, ArcZendeskAssistanceApi.class, OrganizationApi.class})
@Import(OAuth2LoginConfig.class)
class OAuth2LoginConfigTest {

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepositoryMock;
    @MockitoBean
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandlerMock;
    @MockitoBean
    private CustomLogoutHandler customLogoutHandler;
    @MockitoBean
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @MockitoBean
    private ZendeskAssistanceTokenService zendeskAssistanceTokenServiceMock;
    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private TokenStoreService tokenStoreService;
    @MockitoBean
    AccessTokenValidationService accessTokenValidationService;
    @MockitoBean
    AuthorizationRequestRepository authorizationRequestRepository;
    @MockitoBean
    OrganizationFacadeService organizationFacadeServiceMock;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Test
    void givenURLWithoutCodeAndStateWhenWithoutAccessTokenThenRedirectToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/token/oneidentity"))
                .andExpect(status().is(400));
    }

    @Test
    void givenURLWhenWithoutAccessTokenThenRedirectToLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/token/oneidentity?code=fakeCode&state=fakeState"))
                .andExpect(status().is(400));
    }

    @Test
    void givenSecurityURLWhenCallEndpointThenNoRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/token/assistance")
                        .param("userEmail", "someone@email.com"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void givenPublicURLWhenCallEndpointThenOk() throws Exception {
        Long brokerId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/public/brokers/{brokerId}/spontaneous/organizations",brokerId))
                .andExpect(status().is2xxSuccessful());
    }

}