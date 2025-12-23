package it.gov.pagopa.arc.connector.bizevents.paidnotice;

import feign.FeignException;
import feign.Response;
import it.gov.pagopa.arc.connector.bizevents.dto.paidnotice.BizEventsPaidNoticeDetailsDTO;
import it.gov.pagopa.arc.connector.bizevents.dto.paidnotice.BizEventsPaidNoticeListDTO;
import it.gov.pagopa.arc.dto.NoticeRequestDTO;
import it.gov.pagopa.arc.dto.NoticesListResponseDTO;
import it.gov.pagopa.arc.dto.mapper.bizevents.paidnotice.BizEventsPaidNoticeDTO2NoticesListResponseDTOMapper;
import it.gov.pagopa.arc.exception.custom.BizEventsInvocationException;
import it.gov.pagopa.arc.exception.custom.BizEventsPaidNoticeNotFoundException;
import it.gov.pagopa.arc.exception.custom.BizEventsReceiptNotFoundException;
import it.gov.pagopa.arc.exception.custom.BizEventsTooManyRequestException;
import it.gov.pagopa.arc.model.generated.NoticesListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@Service
@Slf4j
public class BizEventsPaidNoticeConnectorImpl implements BizEventsPaidNoticeConnector {

    private static final String ERROR_MESSAGE_INVOCATION_EXCEPTION = "An error occurred handling request from biz-Events";
    private static final String ERROR_MESSAGE_TOO_MANY_REQUEST_EXCEPTION = "Too many request occurred handling request from biz-Events";
    private final String apikey;
    private final BizEventsPaidNoticeRestClient bizEventsPaidNoticeRestClient;
    private final BizEventsPaidNoticeDTO2NoticesListResponseDTOMapper bizEventsPaidNoticeDTO2NoticesListResponseDTOMapper;
    private final JsonMapper jsonMapper;

    public BizEventsPaidNoticeConnectorImpl(@Value("${rest-client.biz-events.paid-notice.api-key}") String apikey,
                                            BizEventsPaidNoticeRestClient bizEventsPaidNoticeRestClient, BizEventsPaidNoticeDTO2NoticesListResponseDTOMapper bizEventsPaidNoticeDTO2NoticesListResponseDTOMapper, JsonMapper jsonMapper) {
        this.apikey = apikey;
        this.bizEventsPaidNoticeRestClient = bizEventsPaidNoticeRestClient;
        this.bizEventsPaidNoticeDTO2NoticesListResponseDTOMapper = bizEventsPaidNoticeDTO2NoticesListResponseDTOMapper;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public NoticesListResponseDTO getPaidNoticeList(String fiscalCode, NoticeRequestDTO noticeRequestDTO) {
        String continuationToken = noticeRequestDTO.getContinuationToken();
        Integer size = noticeRequestDTO.getSize();
        Boolean isPayer = noticeRequestDTO.getPaidByMe();
        Boolean isDebtor = noticeRequestDTO.getRegisteredToMe();
        String orderBy = noticeRequestDTO.getOrderBy();
        String ordering = noticeRequestDTO.getOrdering();

        try {
            Response response = bizEventsPaidNoticeRestClient.paidNoticeList(apikey, fiscalCode, continuationToken , size , isPayer, isDebtor, orderBy, ordering);
            if (response.status() != HttpStatus.OK.value()){
                throw FeignException.errorStatus("getPaidNoticeList", response);
            }
            return extractBizEventsPaidNoticeListDTOAndHeaderFromFeignResponse(response);
        } catch (FeignException e) {
            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                log.info("A {} occurred while handling request getPaidNoticeList from biz-Events: HttpStatus {} - {}",
                        e.getClass(),
                        e.status(),
                        e.getMessage());

                return handleNotFound();
            } else if (e.status() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new BizEventsTooManyRequestException(ERROR_MESSAGE_TOO_MANY_REQUEST_EXCEPTION);
            }else {
                throw new BizEventsInvocationException(ERROR_MESSAGE_INVOCATION_EXCEPTION);
            }
        }

    }

    @Override
    public BizEventsPaidNoticeDetailsDTO getPaidNoticeDetails(String userId, String userFiscalCode, String eventId) {

        try {
            return bizEventsPaidNoticeRestClient.paidNoticeDetails(apikey, userFiscalCode, eventId);
        }catch (FeignException e){
            if(e.status() == HttpStatus.NOT_FOUND.value()){
                throw new BizEventsPaidNoticeNotFoundException("An error occurred handling request from biz-Events to retrieve paid notice with event id [%s] for the current user with userId [%s]".formatted(eventId, userId));
            } else if (e.status() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new BizEventsTooManyRequestException(ERROR_MESSAGE_TOO_MANY_REQUEST_EXCEPTION);
            }else {
                throw new BizEventsInvocationException(ERROR_MESSAGE_INVOCATION_EXCEPTION);
            }
        }
    }

    @Override
    public Resource getPaidNoticeReceipt(String userId, String userFiscalCode, String eventId) {
        try {
            return bizEventsPaidNoticeRestClient.paidNoticeReceipt(apikey, userFiscalCode, eventId);
        }catch (FeignException e){
            if (e.status() == HttpStatus.NOT_FOUND.value()){
                throw  new BizEventsReceiptNotFoundException("An error occurred handling request from biz-Events to retrieve notice receipt with event id [%s] for the current user with userId [%s]".formatted(eventId, userId));
            } else if (e.status() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                throw new BizEventsTooManyRequestException(ERROR_MESSAGE_TOO_MANY_REQUEST_EXCEPTION);
            }else {
                throw new BizEventsInvocationException(ERROR_MESSAGE_INVOCATION_EXCEPTION);
            }
        }
    }

    private NoticesListResponseDTO handleNotFound(){
        NoticesListDTO noticesListDTO = NoticesListDTO.builder()
                        .notices(new ArrayList<>())
                        .build();

        return NoticesListResponseDTO.builder()
                .continuationToken(null)
                .noticesListDTO(noticesListDTO)
                .build();
    }

    private NoticesListResponseDTO extractBizEventsPaidNoticeListDTOAndHeaderFromFeignResponse(Response response){
        String continuationTokenHeader = null;
        BizEventsPaidNoticeListDTO bizEventsPaidNoticeListDTO;
        NoticesListDTO noticeListDTO;

        if(response.body() == null){
            throw new BizEventsInvocationException("Error during processing: response body is null");
        }

        try {
            bizEventsPaidNoticeListDTO = jsonMapper.readValue(response.body().asInputStream(), BizEventsPaidNoticeListDTO.class);
            noticeListDTO = bizEventsPaidNoticeDTO2NoticesListResponseDTOMapper.toNoticeListDTO(bizEventsPaidNoticeListDTO);
        } catch (JacksonException | IOException e) {
            throw new BizEventsInvocationException("Error reading or deserializing the response body");
        }

        if(!response.headers().isEmpty()){
            continuationTokenHeader = response.headers()
                    .getOrDefault("x-continuation-token", Collections.emptyList())
                    .stream()
                    .findFirst()
                    .orElse(null);
        }

        return NoticesListResponseDTO.builder().noticesListDTO(noticeListDTO).continuationToken(continuationTokenHeader).build();
    }

}
