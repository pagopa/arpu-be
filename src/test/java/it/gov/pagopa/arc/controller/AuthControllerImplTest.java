package it.gov.pagopa.arc.controller;

import io.micrometer.tracing.Tracer;
import it.gov.pagopa.arc.config.json.JsonConfig;
import it.gov.pagopa.arc.controller.generated.ArcAuthApi;
import it.gov.pagopa.arc.dto.mapper.UpstreamErrorMapper;
import it.gov.pagopa.arc.exception.custom.InvalidTokenException;
import it.gov.pagopa.arc.fakers.auth.UserInfoDTOFaker;
import it.gov.pagopa.arc.model.generated.ErrorDTO;
import it.gov.pagopa.arc.model.generated.TokenResponse;
import it.gov.pagopa.arc.model.generated.UserInfo;
import it.gov.pagopa.arc.security.JwtAuthenticationFilter;
import it.gov.pagopa.arc.security.RecaptchaFilter;
import it.gov.pagopa.arc.service.AuthService;
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
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {
    ArcAuthApi.class
},  excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
    classes = {JwtAuthenticationFilter.class, RecaptchaFilter.class}),
        excludeAutoConfiguration = {
          SecurityAutoConfiguration.class,
          OAuth2ClientAutoConfiguration.class
        })
@Import(JsonConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerImplTest {

  @Autowired
  private JsonMapper jsonMapper;
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;
  @MockitoBean
  private UpstreamErrorMapper upstreamErrorMapperMock;
  @MockitoBean
  private Tracer tracerMock;

  @Test
  void givenAuthenticatedUserThenRetrieveUserInfo() throws Exception {
    // given
    UserInfo userInfo = UserInfoDTOFaker.mockInstance();

    when(authService.getUserLoginInfo()).thenReturn(userInfo);

    //When
    MvcResult result = mockMvc.perform(
            get("/auth/user")
        ).andExpect(status().is2xxSuccessful())
        .andReturn();

    UserInfo userInfoResponse = jsonMapper.readValue(result.getResponse().getContentAsString(),UserInfo.class);

    //then
    Assertions.assertNotNull(userInfoResponse);
    Assertions.assertEquals(userInfo,userInfoResponse);
  }

  @Test
  void givenInvalidAuthenticationThenThrowException() throws Exception {

    when(authService.getUserLoginInfo()).thenThrow(InvalidTokenException.class);

    //When
    MvcResult result = mockMvc.perform(
            get("/auth/user")
        ).andExpect(status().is4xxClientError())
        .andReturn();

    ErrorDTO error = jsonMapper.readValue(result.getResponse().getContentAsString(), ErrorDTO.class);

    //then
    Assertions.assertNotNull(error);
  }

  @Test
  void getSampleToken() throws Exception {

    when(authService.generateAuthUser()).thenReturn(new TokenResponse());

    //When
    MvcResult result = mockMvc.perform(
            get("/auth/testuser")
        ).andExpect(status().is(200))
        .andReturn();

    TokenResponse tokenResponse = jsonMapper.readValue(result.getResponse().getContentAsString(), TokenResponse.class);

    //then
    Assertions.assertNotNull(tokenResponse);
  }

}