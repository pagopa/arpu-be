package it.gov.pagopa.arc;

import it.gov.pagopa.arc.config.OAuth2LoginConfig;
import it.gov.pagopa.arc.connector.bizevents.paidnotice.BizEventsPaidNoticeRestClient;
import it.gov.pagopa.arc.connector.gpd.GPDRestClient;
import it.gov.pagopa.arc.connector.pullpayment.PullPaymentRestClient;
import it.gov.pagopa.arc.service.AccessTokenBuilderService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.json.JsonAssert;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "logging.level.org.springdoc.core.utils.SpringDocAnnotationsUtils=OFF"
})
@Slf4j
class OpenApiGeneratorTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AccessTokenBuilderService accessTokenBuilderService;
    @MockitoBean
    private BizEventsPaidNoticeRestClient bizEventsPaidNoticeRestClient;
    @MockitoBean
    private GPDRestClient gpdRestClient;
    @MockitoBean
    private PullPaymentRestClient pullPaymentRestClient;
    @MockitoBean
    private OAuth2LoginConfig oAuth2LoginConfig;

    @Test
    void generateAndVerifyCommit() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/v3/api-docs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        String openApiResult = result.getResponse().getContentAsString()
                .replace("\r", "");

        Assertions.assertTrue(openApiResult.startsWith("{\n  \"openapi\" : \"3."));

        Path openApiGeneratedPath = Path.of("openapi/generated.openapi.json");
        boolean toStore=true;
        if(Files.exists(openApiGeneratedPath)){
            String storedOpenApi = Files.readString(openApiGeneratedPath);
            try {
                JsonAssert.comparator(JsonCompareMode.STRICT).assertIsMatch(storedOpenApi, openApiResult);
                toStore=false;
            } catch (Throwable e){
                log.info("Observed the following changes: {}", e.getMessage());
            }
        }
        if(toStore){
            Files.writeString(openApiGeneratedPath, openApiResult, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

        String gitStatus = execCmd("git", "status");
        Assertions.assertFalse(gitStatus.contains("openapi/generated.openapi.json"), "Generated OpenApi not committed");
    }

    public static String execCmd(String... cmd) throws java.io.IOException {
        java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}

