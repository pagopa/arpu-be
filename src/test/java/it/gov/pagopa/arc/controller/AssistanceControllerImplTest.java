package it.gov.pagopa.arc.controller;

import io.micrometer.tracing.Tracer;
import it.gov.pagopa.arc.config.json.JsonConfig;
import it.gov.pagopa.arc.controller.generated.ArcZendeskAssistanceApi;
import it.gov.pagopa.arc.model.generated.ZendeskAssistanceTokenResponse;
import it.gov.pagopa.arc.security.JwtAuthenticationFilter;
import it.gov.pagopa.arc.security.RecaptchaFilter;
import it.gov.pagopa.arc.service.ZendeskAssistanceTokenService;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {
        ArcZendeskAssistanceApi.class},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {JwtAuthenticationFilter.class, RecaptchaFilter.class}),
excludeAutoConfiguration = {
        SecurityAutoConfiguration .class,
        OAuth2ClientAutoConfiguration .class
        })
@Import(JsonConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AssistanceControllerImplTest {

    private static final String FAKE_USER_EMAIL = "someone@email.com";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ZendeskAssistanceTokenService zendeskAssistanceTokenServiceMock;
    @MockitoBean
    private Tracer tracerMock;

    @Test
    void givenUserEmailWhenGetZendeskAssistanceTokenThenReturnZendeskAssistanceToken() throws Exception {
        //given
        String assistanceToken = "fakeAssistanceToken";
        String returnTo = "help-center.com";

        ZendeskAssistanceTokenResponse expected = ZendeskAssistanceTokenResponse
                .builder()
                .assistanceToken(assistanceToken)
                .returnTo(returnTo)
                .build();

        when(zendeskAssistanceTokenServiceMock.retrieveZendeskAssistanceTokenResponse(FAKE_USER_EMAIL)).thenReturn(expected);
        //when
        MvcResult result = mockMvc.perform(
                        get("/token/assistance")
                                .param("userEmail", FAKE_USER_EMAIL)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();

        ZendeskAssistanceTokenResponse resultResponse = TestUtils.jsonMapper.readValue(result.getResponse().getContentAsString(),
                ZendeskAssistanceTokenResponse.class);
        //then
        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(expected,resultResponse);
        verify(zendeskAssistanceTokenServiceMock).retrieveZendeskAssistanceTokenResponse(any());
    }

    @Test
    void givenNoUserEmailWhenGetZendeskAssistanceTokenThenReturnBadRequest() throws Exception {
        mockMvc.perform(
                        get("/token/assistance")
                ).andExpect(status().is4xxClientError())
                .andReturn();
    }
}
