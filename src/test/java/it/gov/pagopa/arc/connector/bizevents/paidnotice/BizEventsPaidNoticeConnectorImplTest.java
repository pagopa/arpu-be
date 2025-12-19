package it.gov.pagopa.arc.connector.bizevents.paidnotice;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import feign.Request;
import feign.Response;
import it.gov.pagopa.arc.config.FeignConfig;
import it.gov.pagopa.arc.config.WireMockConfig;
import it.gov.pagopa.arc.config.json.JsonConfig;
import it.gov.pagopa.arc.connector.bizevents.dto.paidnotice.BizEventsPaidNoticeDTO;
import it.gov.pagopa.arc.connector.bizevents.dto.paidnotice.BizEventsPaidNoticeDetailsDTO;
import it.gov.pagopa.arc.connector.bizevents.dto.paidnotice.BizEventsPaidNoticeListDTO;
import it.gov.pagopa.arc.connector.bizevents.enums.Origin;
import it.gov.pagopa.arc.connector.bizevents.enums.PaymentMethod;
import it.gov.pagopa.arc.dto.NoticeRequestDTO;
import it.gov.pagopa.arc.dto.NoticesListResponseDTO;
import it.gov.pagopa.arc.dto.mapper.bizevents.paidnotice.BizEventsPaidNoticeDTO2NoticesListResponseDTOMapper;
import it.gov.pagopa.arc.exception.custom.BizEventsInvocationException;
import it.gov.pagopa.arc.exception.custom.BizEventsPaidNoticeNotFoundException;
import it.gov.pagopa.arc.exception.custom.BizEventsReceiptNotFoundException;
import it.gov.pagopa.arc.exception.custom.BizEventsTooManyRequestException;
import it.gov.pagopa.arc.fakers.NoticeRequestDTOFaker;
import it.gov.pagopa.arc.model.generated.NoticeDTO;
import it.gov.pagopa.arc.model.generated.NoticesListDTO;
import it.gov.pagopa.arc.utils.MemoryAppender;
import it.gov.pagopa.arc.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static it.gov.pagopa.arc.config.WireMockConfig.WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(
        initializers = WireMockConfig.WireMockInitializer.class,
        classes = {
                BizEventsPaidNoticeConnectorImpl.class,
                JsonConfig.class,
                FeignConfig.class,
                BizEventsPaidNoticeRestClient.class,
                FeignAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class
        })
@TestPropertySource(properties = {
        "rest-client.biz-events.paid-notice.api-key=x_api_key0",
        WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX + "rest-client.biz-events.paid-notice.baseUrl=bizEventsPaidNoticeMock",
        "rest-client.pull-payment.baseUrl=pullPaymentMock",
        "rest-client.gpd.baseUrl=gpdMock"
})
class BizEventsPaidNoticeConnectorImplTest {

    @Autowired
    private BizEventsPaidNoticeConnector bizEventsPaidNoticeConnector;
    @MockitoBean
    private BizEventsPaidNoticeDTO2NoticesListResponseDTOMapper bizEventsPaidNoticeDTO2NoticesListResponseDTOMapperMock;

    private MemoryAppender memoryAppender;

    private final JsonMapper jsonMapper =  TestUtils.jsonMapper;


    @BeforeEach
    void setUp() {
        Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("it.gov.pagopa.arc.connector.bizevents.paidnotice.BizEventsPaidNoticeConnectorImpl");
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    void givenHeaderAndParameterWhenCallBizEventsPaidNoticeConnectorThenReturnPaidNoticeList() {
        //given
        BizEventsPaidNoticeDTO bizEventsPaidNoticeDTO = BizEventsPaidNoticeDTO.builder()
                .eventId("1")
                .payeeName("Comune di Milano")
                .payeeTaxCode("MI_XXX")
                .amount("180,00")
                .noticeDate("2024-03-27T13:07:25Z")
                .isCart(false)
                .isDebtor(true)
                .isPayer(true)
                .build();

        BizEventsPaidNoticeListDTO bizEventsPaidNoticeListDTO = BizEventsPaidNoticeListDTO.builder().notices(List.of(bizEventsPaidNoticeDTO)).build();

        NoticeDTO noticeDTO = NoticeDTO.builder()
                .eventId("1")
                .payeeName("Comune di Milano")
                .payeeTaxCode("MI_XXX")
                .amount(18000L)
                .noticeDate(ZonedDateTime.parse("2024-03-27T13:07:25Z"))
                .isCart(false)
                .paidByMe(true)
                .registeredToMe(true)
                .build();

        NoticesListDTO noticesListDTO = NoticesListDTO.builder().notices(List.of(noticeDTO)).build();

        Mockito.when(bizEventsPaidNoticeDTO2NoticesListResponseDTOMapperMock.toNoticeListDTO(bizEventsPaidNoticeListDTO)).thenReturn(noticesListDTO);
        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTO.builder()
                .continuationToken("CONTINUATION_TOKEN")
                .size(1)
                .paidByMe(true)
                .registeredToMe(true)
                .orderBy("TRANSACTION_DATE")
                .ordering("DESC")
                .build();

        //when
        NoticesListResponseDTO result = bizEventsPaidNoticeConnector.getPaidNoticeList("DUMMY_FISCAL_CODE_OK", noticeRequestDTO);

        //then

        List<NoticeDTO> notices = result.getNoticesListDTO().getNotices();
        assertEquals(1, notices.size());
        assertEquals("1", notices.getFirst().getEventId());
        assertEquals("Comune di Milano", notices.getFirst().getPayeeName());
        assertEquals("MI_XXX", notices.getFirst().getPayeeTaxCode());
        assertEquals(18000L, notices.getFirst().getAmount());
        assertEquals(ZonedDateTime.parse("2024-03-27T13:07:25Z") , notices.getFirst().getNoticeDate());
        assertFalse(notices.getFirst().getIsCart());
        assertTrue(notices.getFirst().getPaidByMe());
        assertTrue(notices.getFirst().getRegisteredToMe());
        assertEquals("continuation-token", result.getContinuationToken());

    }

    @Test
    void givenHeaderAndParameterWhenNotFoundThenReturnEmptyPaidNoticeList() {
        //given
        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        //when
        NoticesListResponseDTO noticesListResponseDTO = bizEventsPaidNoticeConnector.getPaidNoticeList("DUMMY_FISCAL_CODE_NOT_FOUND", noticeRequestDTO);

        //then
        Assertions.assertEquals(0, noticesListResponseDTO.getNoticesListDTO().getNotices().size());
        System.out.println(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage());
        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage()
                .contains(("A class feign.FeignException$NotFound occurred while handling request getPaidNoticeList from biz-Events: HttpStatus 404"))
        );
        assertNull(noticesListResponseDTO.getContinuationToken());
    }

    @Test
    void givenHeaderAndParameterWhenResponseBodyNullThenThrow() {
        // given
        BizEventsPaidNoticeRestClient bizEventsPaidNoticeRestClient = Mockito.mock(BizEventsPaidNoticeRestClient.class);
        Request originalRequest = Request.create(Request.HttpMethod.GET, "http://dummy-url", Collections.emptyMap(), null, null, null);

        Response mockResponse = Response.builder().status(200).request(originalRequest).build();
        Mockito.when(bizEventsPaidNoticeRestClient.paidNoticeList(   anyString(),
                anyString(),
                anyString(),
                anyInt(),
                anyBoolean(),
                anyBoolean(),
                anyString(),
                anyString()))
                .thenReturn(mockResponse);

        bizEventsPaidNoticeConnector = new BizEventsPaidNoticeConnectorImpl("", bizEventsPaidNoticeRestClient, bizEventsPaidNoticeDTO2NoticesListResponseDTOMapperMock, jsonMapper);
        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        // when & then
        BizEventsInvocationException exception = assertThrows(BizEventsInvocationException.class, () ->
                bizEventsPaidNoticeConnector.getPaidNoticeList("fiscalCode", noticeRequestDTO)
        );

        assertEquals("Error during processing: response body is null", exception.getMessage());
        Mockito.verify(bizEventsPaidNoticeRestClient).paidNoticeList("", "fiscalCode", "continuation-token", 2, true, true, "TRANSACTION_DATE", "DESC");
    }

    @Test
    void givenHeaderAndParameterWhenThrowIOExceptionThenThrow() throws IOException {
        // given
        BizEventsPaidNoticeRestClient bizEventsPaidNoticeRestClient = Mockito.mock(BizEventsPaidNoticeRestClient.class);
        Request originalRequest = Request.create(Request.HttpMethod.GET, "http://dummy-url", Collections.emptyMap(), null, null, null);

        JsonMapper mockJsonMapper = Mockito.mock(JsonMapper.class);
        bizEventsPaidNoticeConnector = new BizEventsPaidNoticeConnectorImpl("", bizEventsPaidNoticeRestClient, bizEventsPaidNoticeDTO2NoticesListResponseDTOMapperMock, mockJsonMapper);

        doThrow(JacksonIOException.construct(new IOException("Deserialization error")))
                .when(mockJsonMapper)
                .readValue(any(InputStream.class), eq(BizEventsPaidNoticeListDTO.class));

        Response.Body mockBody = Mockito.mock(Response.Body.class);
        InputStream mockInputStream = Mockito.mock(InputStream.class);

        Mockito.when(mockBody.asInputStream()).thenReturn(mockInputStream);

        Response mockResponse = Response.builder()
                .status(200)
                .request(originalRequest)
                .body(mockBody)
                .build();

        Mockito.when(bizEventsPaidNoticeRestClient.paidNoticeList(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyInt(),
                        anyBoolean(),
                        anyBoolean(),
                        anyString(),
                        anyString()))
                .thenReturn(mockResponse);
        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        // when & then
        BizEventsInvocationException exception = assertThrows(BizEventsInvocationException.class, () ->
                bizEventsPaidNoticeConnector.getPaidNoticeList("fiscalCode", noticeRequestDTO )
        );

        assertEquals("Error reading or deserializing the response body", exception.getMessage());
    }



    @Test
    void givenHeaderAndParameterWhenErrorThenThrowBizEventsInvocationException() {
        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        //When
        //Then
        BizEventsInvocationException exception = assertThrows(BizEventsInvocationException.class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeList("DUMMY_FISCAL_CODE_ERROR", noticeRequestDTO));
        Assertions.assertEquals("An error occurred handling request from biz-Events",exception.getMessage());
    }

    @Test
    void givenHeaderAndParameterWhenErrorThenThrowBizEventsTooManyRequestException() {
        NoticeRequestDTO noticeRequestDTO = NoticeRequestDTOFaker.mockInstance();
        //When
        //Then
        BizEventsTooManyRequestException exception = assertThrows(BizEventsTooManyRequestException.class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeList("DUMMY_FISCAL_CODE_TOO_MANY_REQUEST", noticeRequestDTO));
        Assertions.assertEquals("Too many request occurred handling request from biz-Events",exception.getMessage());
    }

    @Test
    void givenEventIdWhenCallBizEventsPaidNoticeConnectorThenReturnPaidNoticeDetails() {
        //When
        BizEventsPaidNoticeDetailsDTO paidNoticeDetails = bizEventsPaidNoticeConnector.getPaidNoticeDetails("USER_ID", "DUMMY_FISCAL_CODE_PAID_NOTICE_DETAILS", "EVENT_ID_OK_1");
        //then
        Assertions.assertNotNull(paidNoticeDetails.getInfoNotice());
        Assertions.assertEquals(1, paidNoticeDetails.getCarts().size());
        Assertions.assertEquals("EVENT_ID_OK_1", paidNoticeDetails.getInfoNotice().getEventId());
        Assertions.assertEquals("250863", paidNoticeDetails.getInfoNotice().getAuthCode());
        Assertions.assertEquals("223560110624", paidNoticeDetails.getInfoNotice().getRrn());
        Assertions.assertEquals("2024-06-13T15:22:04Z", paidNoticeDetails.getInfoNotice().getNoticeDate());
        Assertions.assertEquals("Worldline Merchant Services Italia S.p.A.", paidNoticeDetails.getInfoNotice().getPspName());

        Assertions.assertEquals("ERNESTO HOLDER", paidNoticeDetails.getInfoNotice().getWalletInfo().getAccountHolder());
        Assertions.assertEquals("MASTERCARD", paidNoticeDetails.getInfoNotice().getWalletInfo().getBrand());
        Assertions.assertEquals("0403", paidNoticeDetails.getInfoNotice().getWalletInfo().getBlurredNumber());

        Assertions.assertEquals(PaymentMethod.PO, paidNoticeDetails.getInfoNotice().getPaymentMethod());
        Assertions.assertEquals("ERNESTO PAYER", paidNoticeDetails.getInfoNotice().getPayer().getName());
        Assertions.assertEquals("TAX_CODE", paidNoticeDetails.getInfoNotice().getPayer().getTaxCode());

        Assertions.assertEquals("634.37", paidNoticeDetails.getInfoNotice().getAmount());
        Assertions.assertEquals("0.53", paidNoticeDetails.getInfoNotice().getFee());
        Assertions.assertEquals(Origin.UNKNOWN, paidNoticeDetails.getInfoNotice().getOrigin());

        Assertions.assertEquals("pagamento", paidNoticeDetails.getCarts().getFirst().getSubject());
        Assertions.assertEquals("634.37", paidNoticeDetails.getCarts().getFirst().getAmount());

        Assertions.assertEquals("ACI Automobile Club Italia", paidNoticeDetails.getCarts().getFirst().getPayee().getName());
        Assertions.assertEquals("00493410583", paidNoticeDetails.getCarts().getFirst().getPayee().getTaxCode());

        Assertions.assertEquals("ERNESTO PAYER", paidNoticeDetails.getCarts().getFirst().getDebtor().getName());
        Assertions.assertEquals("TAX_CODE", paidNoticeDetails.getCarts().getFirst().getDebtor().getTaxCode());

        Assertions.assertEquals("960000000094659945", paidNoticeDetails.getCarts().getFirst().getRefNumberValue());
        Assertions.assertEquals("IUV", paidNoticeDetails.getCarts().getFirst().getRefNumberType());
    }

    @Test
    void givenEventIdWhenNotFoundThenReturnException() {
        //given
        //when
        BizEventsPaidNoticeNotFoundException bizEventsPaidNoticeNotFoundException = assertThrows(BizEventsPaidNoticeNotFoundException.class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeDetails("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_DETAILS_NOT_FOUND", "EVENT_ID_NOT_FOUND_1"));
        Assertions.assertEquals("An error occurred handling request from biz-Events to retrieve paid notice with event id [EVENT_ID_NOT_FOUND_1] for the current user with userId [USER_ID]", bizEventsPaidNoticeNotFoundException.getMessage());
    }

    @Test
    void givenEventIdWhenErrorThenThrowBizEventsInvocationException() {
        //When
        //Then
        BizEventsInvocationException bizEventsInvocationException = assertThrows(BizEventsInvocationException.class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeDetails("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_DETAILS_ERROR", "EVENT_ID_ERROR_1"));
        Assertions.assertEquals("An error occurred handling request from biz-Events", bizEventsInvocationException.getMessage());
    }

    @Test
    void givenHeaderAndParameterWhenCallDetailsThenThrowBizEventsTooManyRequestException() {
        //When
        //Then
        BizEventsTooManyRequestException bizEventsTooManyRequestException = assertThrows(BizEventsTooManyRequestException .class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeDetails("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_DETAILS_TOO_MANY_REQUEST", "EVENT_ID_ERROR_1"));
        Assertions.assertEquals("Too many request occurred handling request from biz-Events", bizEventsTooManyRequestException.getMessage());
    }

    @Test
    void givenEventIdWhenCallBizEventsPaidNoticeConnectorThenReturnNoticeReceipt() throws IOException {
        //given
        //when
        Resource noticeReceipt = bizEventsPaidNoticeConnector.getPaidNoticeReceipt("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_RECEIPT_OK", "EVENT_ID_OK_1");

        //then
        Assertions.assertNotNull(noticeReceipt);
        Assertions.assertTrue(noticeReceipt.exists());
        byte[] expectedContent = Files.readAllBytes(Paths.get("src/test/resources/stub/__files/testReceiptPdfFile.pdf"));
        byte[] actualContent = noticeReceipt.getInputStream().readAllBytes();
        assertArrayEquals(expectedContent, actualContent);
    }

    @Test
    void givenEventIdWhenReceiptNotFoundThenReturnException() {
        //given
        //when
        BizEventsReceiptNotFoundException bizEventsReceiptNotFoundException = assertThrows(BizEventsReceiptNotFoundException.class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeReceipt("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_RECEIPT_NOT_FOUND", "EVENT_ID_NOT_FOUND_1"));
        Assertions.assertEquals("An error occurred handling request from biz-Events to retrieve notice receipt with event id [EVENT_ID_NOT_FOUND_1] for the current user with userId [USER_ID]", bizEventsReceiptNotFoundException.getMessage());
    }

    @Test
    void givenEventIdWhenReceiptErrorThenThrowBizEventsInvocationException() {
        //When
        //Then
        BizEventsInvocationException bizEventsInvocationException = assertThrows(BizEventsInvocationException.class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeReceipt("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_RECEIPT_ERROR", "EVENT_ID_ERROR_2"));
        Assertions.assertEquals("An error occurred handling request from biz-Events", bizEventsInvocationException.getMessage());
    }

    @Test
    void givenHeaderAndParameterWhenCallReceiptThenThrowBizEventsTooManyRequestException() {
        //When
        //Then
        BizEventsTooManyRequestException bizEventsTooManyRequestException = assertThrows(BizEventsTooManyRequestException .class,
                () -> bizEventsPaidNoticeConnector.getPaidNoticeReceipt("USER_ID","DUMMY_FISCAL_CODE_PAID_NOTICE_RECEIPT_TOO_MANY_REQUEST", "EVENT_ID_ERROR_2"));
        Assertions.assertEquals("Too many request occurred handling request from biz-Events", bizEventsTooManyRequestException.getMessage());
    }
}