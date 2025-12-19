package it.gov.pagopa.arc.connector.gpd;

import it.gov.pagopa.arc.config.FeignConfig;
import it.gov.pagopa.arc.config.WireMockConfig;
import it.gov.pagopa.arc.connector.gpd.dto.GPDPaymentNoticeDetailsDTO;
import it.gov.pagopa.arc.connector.gpd.dto.GPDPaymentNoticePayloadDTO;
import it.gov.pagopa.arc.connector.gpd.dto.GPDPaymentOptionPayloadDTO;
import it.gov.pagopa.arc.connector.gpd.dto.GPDTransferPayloadDTO;
import it.gov.pagopa.arc.connector.gpd.enums.GPDPaymentNoticeStatus;
import it.gov.pagopa.arc.exception.custom.GPDInvalidRequestException;
import it.gov.pagopa.arc.exception.custom.GPDInvocationException;
import it.gov.pagopa.arc.exception.custom.GPDPaymentNoticeDetailsNotFoundException;
import it.gov.pagopa.arc.exception.custom.GPDTooManyRequestException;
import it.gov.pagopa.arc.fakers.connector.gpd.GPDPaymentNoticeDetailsDTOFaker;
import it.gov.pagopa.arc.fakers.connector.gpd.GPDPaymentNoticePayloadDTOFaker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static it.gov.pagopa.arc.config.WireMockConfig.WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(
        initializers = WireMockConfig.WireMockInitializer.class,
        classes = {
                GPDConnectorImpl.class,
                FeignConfig.class,
                GPDRestClient.class,
                FeignAutoConfiguration.class,
                HttpMessageConvertersAutoConfiguration.class
        })
@TestPropertySource(properties = {
        WIREMOCK_TEST_PROP2BASEPATH_MAP_PREFIX + "rest-client.gpd.baseUrl=gpdMock",
        "rest-client.gpd.api-key=x_api_key0",
        "rest-client.pull-payment.baseUrl=pullPaymentMock",
        "rest-client.biz-events.paid-notice.baseUrl=bizEventsPaidNoticeMock"
})
class GPDConnectorImplTest {

    @Autowired
    private GPDConnector gpdConnector;

    @Test
    void givenPDWithSinglePOWhenGetPaymentNoticeDetailsThenReturnGPDPaymentNoticeDetails() {
        //given
        GPDPaymentNoticeDetailsDTO gpdPaymentNoticeDetailsDTOExpected = GPDPaymentNoticeDetailsDTOFaker.mockInstance(1, false);

        //when
        GPDPaymentNoticeDetailsDTO result = gpdConnector.getPaymentNoticeDetails("USER_ID", "DUMMY_ORGANIZATION_FISCAL_CODE", "IUPD_OK_0");

        //then
        assertNotNull(result);
        assertEquals(gpdPaymentNoticeDetailsDTOExpected, result);
        assertEquals(1, result.getPaymentOption().size());
        assertFalse(result.getPaymentOption().getFirst().getIsPartialPayment());

    }

    @Test
    void givenPDWithInstallmentsPOWhenGetPaymentNoticeDetailsThenReturnGPDPaymentNoticeDetails() {
        //given
        GPDPaymentNoticeDetailsDTO gpdPaymentNoticeDetailsDTOExpected = GPDPaymentNoticeDetailsDTOFaker.mockInstance(2, true);

        //when
        GPDPaymentNoticeDetailsDTO result = gpdConnector.getPaymentNoticeDetails("USER_ID", "DUMMY_ORGANIZATION_FISCAL_CODE", "IUPD_INSTALLMENTS_OK_2");
        System.out.println(gpdPaymentNoticeDetailsDTOExpected);
        //then
        assertNotNull(result);
        assertEquals(gpdPaymentNoticeDetailsDTOExpected, result);
        assertEquals(2, result.getPaymentOption().size());
        assertTrue(result.getPaymentOption().getFirst().getIsPartialPayment());
        assertTrue(result.getPaymentOption().getLast().getIsPartialPayment());
    }

    @Test
    void givenNotFoundIUPDWhenGetPaymentNoticeDetailsThenThrowException() {
        GPDPaymentNoticeDetailsNotFoundException ex = assertThrows(GPDPaymentNoticeDetailsNotFoundException.class,
                () -> gpdConnector.getPaymentNoticeDetails("USER_ID", "DUMMY_ORGANIZATION_FISCAL_CODE", "IUPD_NOT_FOUND_0"));
        assertEquals("An error occurred handling request from GPD to retrieve payment notice with organizationFiscalCode [DUMMY_ORGANIZATION_FISCAL_CODE] and iupd [IUPD_NOT_FOUND_0] for the current user with userId [USER_ID]", ex.getMessage());
    }

    @Test
    void givenWrongIUPDWhenGetPaymentNoticeDetailsThenThrowException() {
        GPDInvalidRequestException ex = assertThrows(GPDInvalidRequestException.class,
                () -> gpdConnector.getPaymentNoticeDetails("USER_ID", "DUMMY_ORGANIZATION_FISCAL_CODE", "IUPD_BAD_REQUEST_0"));
        assertEquals("One or more inputs provided during the request to GPD are invalid", ex.getMessage());
    }

    @Test
    void givenErrorIUPDWhenGetPaymentNoticeDetailsThenThrowException() {
        GPDInvocationException ex = assertThrows(GPDInvocationException.class,
                () -> gpdConnector.getPaymentNoticeDetails("USER_ID", "DUMMY_ORGANIZATION_FISCAL_CODE", "IUPD_ERROR_0"));
        assertEquals("An error occurred handling request from GPD service", ex.getMessage());
    }

    @Test
    void givenIUPDWhenGetPaymentNoticeDetailsThenThrowTooManyRequestException() {
        GPDTooManyRequestException ex = assertThrows(GPDTooManyRequestException.class,
                () -> gpdConnector.getPaymentNoticeDetails("USER_ID", "DUMMY_ORGANIZATION_FISCAL_CODE", "IUPD_TOO_MANY_REQUEST_0"));
        assertEquals("Too many request occurred handling request from GPD", ex.getMessage());
    }

    @Test
    void givenPDDataWhenPostGeneratePaymentNoticeThenReturnGPDPaymentNoticePayloadDTO() {
        //given
        GPDPaymentNoticePayloadDTO dummyPaymentNoticePayload = GPDPaymentNoticePayloadDTOFaker.mockInstance("DUMMY_ORGANIZATION_FISCAL_CODE_OK");

        GPDPaymentNoticePayloadDTO expected = GPDPaymentNoticePayloadDTOFaker.mockInstance("DUMMY_ORGANIZATION_FISCAL_CODE_OK");
        expected.setFiscalCode("USER_DUMMY_FISCAL_CODE");
        expected.setFullName("USER_FULL_NAME");
        expected.setValidityDate(LocalDateTime.parse("2025-01-28T10:23:51.512312545"));
        expected.setStatus(GPDPaymentNoticeStatus.VALID);

        GPDPaymentOptionPayloadDTO gpdPaymentOptionPayloadDTO = expected.getPaymentOption().getFirst();
        gpdPaymentOptionPayloadDTO.setNav("3".concat(gpdPaymentOptionPayloadDTO.getIuv()));
        gpdPaymentOptionPayloadDTO.setFee(0L);
        gpdPaymentOptionPayloadDTO.setNotificationFee(0L);
        gpdPaymentOptionPayloadDTO.setPaymentOptionMetadata(new ArrayList<>());

        GPDTransferPayloadDTO transfer = gpdPaymentOptionPayloadDTO.getTransfer().getFirst();
        transfer.setTransferMetadata(new ArrayList<>());
        gpdPaymentOptionPayloadDTO.setTransfer(List.of(transfer));
        expected.setPaymentOption(List.of(gpdPaymentOptionPayloadDTO));

        //when
        GPDPaymentNoticePayloadDTO result = gpdConnector.generatePaymentNotice("DUMMY_ORGANIZATION_FISCAL_CODE_OK", dummyPaymentNoticePayload);

        //then
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals(1, result.getPaymentOption().size());
        assertFalse(result.getPaymentOption().getFirst().getIsPartialPayment());

    }

    @Test
    void givenWrongPayloaWhenGetPaymentNoticeDetailsThenThrowException() {
        GPDPaymentNoticePayloadDTO dummyPaymentNoticePayload = new GPDPaymentNoticePayloadDTO();
        dummyPaymentNoticePayload.setIupd("DUMMY_ORGANIZATION_FISCAL_CODE_BAD_REQUEST-1234567890");

        GPDInvalidRequestException ex = assertThrows(GPDInvalidRequestException.class,
                () -> gpdConnector.generatePaymentNotice("DUMMY_ORGANIZATION_FISCAL_CODE_BAD_REQUEST", dummyPaymentNoticePayload));
        assertEquals("One or more inputs provided during the request to generate payment notice to GPD are invalid", ex.getMessage());
    }

    @Test
    void givenWrongPayloaWhenGetPaymentNoticeDetailsThenThrowInternalErrorException() {
        GPDPaymentNoticePayloadDTO dummyPaymentNoticePayload = new GPDPaymentNoticePayloadDTO();
        dummyPaymentNoticePayload.setIupd("DUMMY_ORGANIZATION_FISCAL_CODE_ERROR-1234567890");

        GPDInvocationException ex = assertThrows(GPDInvocationException.class,
                () -> gpdConnector.generatePaymentNotice("DUMMY_ORGANIZATION_FISCAL_CODE_ERROR", dummyPaymentNoticePayload));
        assertEquals("An error occurred handling request from GPD service", ex.getMessage());
    }

    @Test
    void givenPayloaWhenGetPaymentNoticeDetailsThenThrowTooManyRequestException() {
        GPDPaymentNoticePayloadDTO dummyPaymentNoticePayload = new GPDPaymentNoticePayloadDTO();
        dummyPaymentNoticePayload.setIupd("DUMMY_ORGANIZATION_FISCAL_CODE_TOO_MANY_REQUEST-1234567890");

        GPDTooManyRequestException ex = assertThrows(GPDTooManyRequestException.class,
                () -> gpdConnector.generatePaymentNotice("DUMMY_ORGANIZATION_FISCAL_CODE_TOO_MANY_REQUEST", dummyPaymentNoticePayload));
        assertEquals("Too many request occurred handling request from GPD", ex.getMessage());
    }
}