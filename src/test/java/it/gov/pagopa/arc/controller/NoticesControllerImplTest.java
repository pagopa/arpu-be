package it.gov.pagopa.arc.controller;

import it.gov.pagopa.arc.config.json.JsonConfig;
import it.gov.pagopa.arc.controller.generated.ArcNoticesApi;
import it.gov.pagopa.arc.dto.NoticeRequestDTO;
import it.gov.pagopa.arc.dto.NoticesListResponseDTO;
import it.gov.pagopa.arc.dto.mapper.NoticeRequestDTOMapper;
import it.gov.pagopa.arc.fakers.NoticeDTOFaker;
import it.gov.pagopa.arc.fakers.NoticeDetailsDTOFaker;
import it.gov.pagopa.arc.fakers.NoticeRequestDTOFaker;
import it.gov.pagopa.arc.fakers.auth.IamUserInfoDTOFaker;
import it.gov.pagopa.arc.model.generated.NoticeDTO;
import it.gov.pagopa.arc.model.generated.NoticeDetailsDTO;
import it.gov.pagopa.arc.model.generated.NoticesListDTO;
import it.gov.pagopa.arc.security.JwtAuthenticationFilter;
import it.gov.pagopa.arc.service.NoticesService;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {
        ArcNoticesApi.class
}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = JwtAuthenticationFilter.class),
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class
        })
@Import(JsonConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class NoticesControllerImplTest {
    private static final int SIZE = 2;
    private static final String CONTINUATION_TOKEN = "continuation-token";
    private static final String ORDER_BY = "TRANSACTION_DATE";
    private static final String ORDERING = "DESC";
    private static final String DUMMY_FISCAL_CODE = "FISCAL-CODE789456";
    private static final String USER_ID = "user_id";
    private static final String EVENT_ID = "event_id";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoticesService noticesServiceMock;
    @MockitoBean
    private NoticeRequestDTOMapper noticeRequestDTOMapper;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                IamUserInfoDTOFaker.mockInstance(), null, null);
        authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TestUtils.clearDefaultTimezone();
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenFiscalCodeWhenCallGetNoticesListThenReturnNoticesList() throws Exception {
        //Given
        NoticeDTO noticeDTO = NoticeDTOFaker.mockInstance(1, false);
        NoticesListDTO noticesListDTO = NoticesListDTO.builder().notices(List.of(noticeDTO)).build();
        NoticesListResponseDTO noticesListResponseDTO = NoticesListResponseDTO.builder()
                .noticesListDTO(noticesListDTO)
                .continuationToken(CONTINUATION_TOKEN).build();

        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        noticeRequestDTO.setContinuationToken(CONTINUATION_TOKEN);

        Mockito.when(noticeRequestDTOMapper.apply(CONTINUATION_TOKEN, SIZE, true, true, ORDER_BY, ORDERING)).thenReturn(noticeRequestDTO);
        Mockito.when(noticesServiceMock.retrieveNoticesAndToken(DUMMY_FISCAL_CODE, USER_ID, noticeRequestDTO)).thenReturn(noticesListResponseDTO);
        //When
        MvcResult result = mockMvc.perform(
                        get("/notices")
                                .header("x-continuation-token", CONTINUATION_TOKEN)
                                .param("size", String.valueOf(SIZE))
                                .param("paidByMe", String.valueOf(true))
                                .param("registeredToMe", String.valueOf(true))
                                .param("orderBy", ORDER_BY)
                                .param("ordering", ORDERING)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(header().string("x-continuation-token", CONTINUATION_TOKEN))
                .andReturn();

        NoticesListDTO resultResponse = TestUtils.jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                NoticesListDTO.class);


        //Then
        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(noticesListDTO, resultResponse);
    }

    @Test
    void givenFiscalCodeWhenCallGetNoticesListThenReturnNoticesListWithNullContinuationToken() throws Exception {
        //Given
        NoticeDTO noticeDTO = NoticeDTOFaker.mockInstance(2, false);
        noticeDTO.setRegisteredToMe(false);
        NoticesListDTO noticesListDTO = NoticesListDTO.builder().notices(List.of(noticeDTO)).build();
        NoticesListResponseDTO noticesListResponseDTO = NoticesListResponseDTO.builder()
                .noticesListDTO(noticesListDTO)
                .continuationToken(null).build();

        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        noticeRequestDTO.setRegisteredToMe(false);
        noticeRequestDTO.setContinuationToken(CONTINUATION_TOKEN);

        Mockito.when(noticeRequestDTOMapper.apply(CONTINUATION_TOKEN, SIZE, true, false, ORDER_BY, ORDERING)).thenReturn(noticeRequestDTO);
        Mockito.when(noticesServiceMock.retrieveNoticesAndToken(DUMMY_FISCAL_CODE, USER_ID, noticeRequestDTO)).thenReturn(noticesListResponseDTO);
        //When
        MvcResult result = mockMvc.perform(
                        get("/notices")
                                .header("x-continuation-token", CONTINUATION_TOKEN)
                                .param("size", String.valueOf(SIZE))
                                .param("paidByMe", String.valueOf(true))
                                .param("registeredToMe", String.valueOf(false))
                                .param("orderBy", ORDER_BY)
                                .param("ordering", ORDERING)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(header().string("x-continuation-token", nullValue()))
                .andReturn();

        NoticesListDTO resultResponse = TestUtils.jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                NoticesListDTO.class);

        //Then
        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(noticesListDTO, resultResponse);
    }

    @Test
    void givenEventIdWhenCallGetNoticeDetailsThenReturnNoticeDetails() throws Exception {
        //Given
        NoticeDetailsDTO noticeDetailsDTO = NoticeDetailsDTOFaker.mockInstance();

        Mockito.when(noticesServiceMock.retrieveNoticeDetails(USER_ID, DUMMY_FISCAL_CODE, EVENT_ID)).thenReturn(noticeDetailsDTO);
        //When
        MvcResult result = mockMvc.perform(
                        get("/notices/{eventId}", EVENT_ID)
                ).andExpect(status().is2xxSuccessful())
                .andReturn();

        NoticeDetailsDTO resultResponse = TestUtils.jsonMapper.readValue(
                result.getResponse().getContentAsString(),
                NoticeDetailsDTO.class);


        //Then
        Assertions.assertNotNull(resultResponse);
        Assertions.assertEquals(noticeDetailsDTO, resultResponse);
    }

    @Test
    void givenEventIdWhenCallGetNoticeReceiptThenReturnNoticeReceipt() throws Exception {
        //Given
        Resource receipt = new FileSystemResource("src/test/resources/stub/__files/testReceiptPdfFile.pdf");

        Mockito.when(noticesServiceMock.retrieveNoticeReceipt(USER_ID, DUMMY_FISCAL_CODE, EVENT_ID)).thenReturn(receipt);

        //When
        MvcResult result = mockMvc.perform(
                        get("/notices/{eventId}/receipt", EVENT_ID)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"testReceiptPdfFile.pdf\""))
                .andReturn();

        //Then
        byte[] expectedContent = Files.readAllBytes(Paths.get("src/test/resources/stub/__files/testReceiptPdfFile.pdf"));
        byte[] actualContent = result.getResponse().getContentAsByteArray();

        Assertions.assertNotNull(actualContent);
        Assertions.assertArrayEquals(expectedContent, actualContent);
    }

    @Test
    void givenEventIdWhenCallGetNoticeReceiptThenReturnNoticeReceiptWithNullFileName() throws Exception {
        //Given
        Resource receipt = new FileSystemResource("src/test/resources/stub/__files/testReceiptPdfFile.pdf") {
            @Override
            public String getFilename() {
                return null;
            }
        };

        Mockito.when(noticesServiceMock.retrieveNoticeReceipt(USER_ID, DUMMY_FISCAL_CODE, EVENT_ID)).thenReturn(receipt);

        //When
        mockMvc.perform(
                        get("/notices/{eventId}/receipt", EVENT_ID)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "inline"))
                .andReturn();

    }

    @Test
    void givenEventIdWhenCallGetNoticeReceiptThenReturnNoticeReceiptWithEmptyFileName() throws Exception {
        //Given
        Resource receipt = new FileSystemResource("src/test/resources/stub/__files/testReceiptPdfFile.pdf") {
            @Override
            public String getFilename() {
                return "";
            }
        };

        Mockito.when(noticesServiceMock.retrieveNoticeReceipt(USER_ID, DUMMY_FISCAL_CODE, EVENT_ID)).thenReturn(receipt);

        //When
        mockMvc.perform(
                        get("/notices/{eventId}/receipt", EVENT_ID)
                ).andExpect(status().is2xxSuccessful())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"\""))
                .andReturn();
    }
}