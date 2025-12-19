package it.gov.pagopa.arc.connector.pullpayment;

import ch.qos.logback.classic.LoggerContext;
import it.gov.pagopa.arc.config.FeignConfig;
import it.gov.pagopa.arc.config.WireMockConfig;
import it.gov.pagopa.arc.connector.pullpayment.dto.PullPaymentNoticeDTO;
import it.gov.pagopa.arc.exception.custom.PullPaymentInvalidRequestException;
import it.gov.pagopa.arc.exception.custom.PullPaymentInvocationException;
import it.gov.pagopa.arc.exception.custom.PullPaymentTooManyRequestException;
import it.gov.pagopa.arc.fakers.connector.pullPayment.PullPaymentNoticeDTOFaker;
import it.gov.pagopa.arc.utils.MemoryAppender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static it.gov.pagopa.arc.config.WireMockConfig.WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(
        initializers = WireMockConfig.WireMockInitializer.class,
        classes = {
                PullPaymentConnectorImpl.class,
                FeignConfig.class,
                PullPaymentRestClient.class,
                FeignAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class
        })
@TestPropertySource(
        properties = {
                "rest-client.pull-payment.api-key=x_api_key0",
                WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX + "rest-client.pull-payment.baseUrl=pullPaymentMock",
                "rest-client.pull-payment.api-key=x_api_key0",
                "rest-client.biz-events.paid-notice.baseUrl=bizEventsPaidNoticeMock",
                "rest-client.gpd.baseUrl=gpdMock"
        })
class PullPaymentConnectorImplTest {
    private static final LocalDate LOCAL_DATE = LocalDate.parse("2024-04-11");
    @Autowired
    private PullPaymentConnector pullPaymentConnector;

    private MemoryAppender memoryAppender;

    @BeforeEach
    void setUp() {
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("it.gov.pagopa.arc.connector.pullpayment.PullPaymentConnectorImpl");
        memoryAppender = new MemoryAppender();
        memoryAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(ch.qos.logback.classic.Level.INFO);
        logger.addAppender(memoryAppender);
        memoryAppender.start();
    }

    @Test
    void givenHeaderAndParameterWhenCallPullPaymentConnectorThenReturnPaymentNoticesList() {
        //given
        PullPaymentNoticeDTO pullPaymentNoticeDTO = PullPaymentNoticeDTOFaker.mockInstance(true);
        //when
        List<PullPaymentNoticeDTO> result = pullPaymentConnector.getPaymentNotices("DUMMY_FISCAL_CODE", LocalDate.now(), 10, 0);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(pullPaymentNoticeDTO,result.getFirst());

    }

    @Test
    void givenHeaderAndParameterWhenCallPullPaymentConnectorThenReturnEmptyPaymentNotices() {
        //given
        //when
        List<PullPaymentNoticeDTO> result = pullPaymentConnector.getPaymentNotices("DUMMY_FISCAL_CODE_NOT_FOUND", LocalDate.now(), 10, 0);

        //then
        Assertions.assertEquals(0,result.size());
        Assertions.assertTrue(memoryAppender.getLoggedEvents().getFirst().getFormattedMessage()
                .contains(("A class feign.FeignException$NotFound occurred handling request getPaymentNotices from pull-payment: HttpStatus 404 - [404 Not Found]"))
        );
    }

    @Test
    void givenHeaderAndParameterWhenBadRequestErrorThenThrowPullPaymentInvalidRequestException() {
        //When
        //Then
        PullPaymentInvalidRequestException pullPaymentInvalidRequestException = Assertions.assertThrows(PullPaymentInvalidRequestException.class,
                () -> pullPaymentConnector.getPaymentNotices("DUMMY_FISCAL_CODE_BAD_REQUEST", LOCAL_DATE, 1000, 0));
        Assertions.assertEquals( "One or more inputs provided during the request from pull payment are invalid", pullPaymentInvalidRequestException.getMessage());
    }

    @Test
    void givenHeaderAndParameterWhenErrorThenThrowPullPaymentInvocationException() {
        //When
        //Then
        PullPaymentInvocationException pullPaymentInvocationException = Assertions.assertThrows(PullPaymentInvocationException.class,
                () -> pullPaymentConnector.getPaymentNotices("DUMMY_FISCAL_CODE_ERROR", LOCAL_DATE, 10, 0));
        Assertions.assertEquals( "An error occurred handling request from pull payment service", pullPaymentInvocationException.getMessage());
    }

    @Test
    void givenHeaderAndParameterWhenTooManyRequestThenThrowPullPaymentTooManyRequestException() {
        //When
        //Then
        PullPaymentTooManyRequestException pullPaymentInvocationException = Assertions.assertThrows(PullPaymentTooManyRequestException.class,
                () -> pullPaymentConnector.getPaymentNotices("DUMMY_FISCAL_CODE_TOO_MANY_REQUEST", LOCAL_DATE, 10, 0));
        Assertions.assertEquals( "Too many request occurred handling request from pull payment service", pullPaymentInvocationException.getMessage());
    }
}