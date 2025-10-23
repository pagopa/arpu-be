package it.gov.pagopa.arc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.arc.fakers.auth.IamUserInfoDTOFaker;
import it.gov.pagopa.arc.fakers.spontaneous.OrganizationsListDTOFaker;
import it.gov.pagopa.arc.model.generated.OrganizationsListDTO;
import it.gov.pagopa.arc.security.JwtAuthenticationFilter;
import it.gov.pagopa.arc.service.PaymentNoticeSpontaneousService;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {
        PaymentNoticeSpontaneousControllerImpl.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class),
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
class PaymentNoticeSpontaneousControllerImplTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentNoticeSpontaneousService paymentNoticeSpontaneousService;

    private static final String USER_ID = "user_id";

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                IamUserInfoDTOFaker.mockInstance(), null, null);
        authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenRequestWhenGetOrganizationsThenReturnOrganizationsList() throws Exception {
        //given
        OrganizationsListDTO organizationsListDTO = OrganizationsListDTOFaker.mockInstance(5);

        Mockito.when(paymentNoticeSpontaneousService.retrieveOrganizations(USER_ID)).thenReturn(organizationsListDTO);

        //when
        MvcResult result = mockMvc.perform(
                        get("/organizations")
                ).andExpect(status().is2xxSuccessful())
                .andReturn();

        OrganizationsListDTO resultResponse = TestUtils.objectMapper.readValue(result.getResponse().getContentAsString(),
                OrganizationsListDTO.class);

        //then
        assertNotNull(resultResponse);
        assertEquals(5, resultResponse.getOrganizations().size());
        assertEquals(organizationsListDTO, resultResponse);
    }
}