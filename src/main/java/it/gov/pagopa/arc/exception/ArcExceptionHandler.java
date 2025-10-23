package it.gov.pagopa.arc.exception;

import it.gov.pagopa.arc.exception.custom.*;
import it.gov.pagopa.arc.model.generated.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ArcExceptionHandler {

    private static final String VALIDATION_ERROR_DEBUG_MESSAGE = "Something went wrong while validating http request";

    @ExceptionHandler(BizEventsInvocationException.class)
    public ResponseEntity<ErrorDTO> handleBizEventsInvocationException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.ErrorEnum.GENERIC_ERROR);
    }

    @ExceptionHandler(BizEventsReceiptNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleBizEventsReceiptNotFoundException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.NOT_FOUND, ErrorDTO.ErrorEnum.RECEIPT_NOT_FOUND_ERROR);
    }

    @ExceptionHandler(BizEventsInvalidAmountException.class)
    public ResponseEntity<ErrorDTO> handleBizEventsInvalidAmountException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.ErrorEnum.INVALID_AMOUNT);
    }

    @ExceptionHandler(BizEventsInvalidDateException.class)
    public ResponseEntity<ErrorDTO> handleBizEventsInvalidDateException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.ErrorEnum.INVALID_DATE);
    }

    @ExceptionHandler(BizEventsTooManyRequestException.class)
    public ResponseEntity<ErrorDTO> handleBizEventsTooManyRequestException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.TOO_MANY_REQUESTS, ErrorDTO.ErrorEnum.TOO_MANY_REQUEST);
    }

    @ExceptionHandler(PullPaymentInvalidRequestException.class)
    public ResponseEntity<ErrorDTO> handlePullPaymentInvalidRequestException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @ExceptionHandler(PullPaymentInvocationException.class)
    public ResponseEntity<ErrorDTO> handlePullPaymentInvocationException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.ErrorEnum.GENERIC_ERROR);
    }
    @ExceptionHandler(PullPaymentTooManyRequestException.class)
    public ResponseEntity<ErrorDTO> handlePullPaymentTooManyRequestException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.TOO_MANY_REQUESTS, ErrorDTO.ErrorEnum.TOO_MANY_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDTO> handlePullInvalidTokenException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.UNAUTHORIZED, ErrorDTO.ErrorEnum.AUTH_USER_UNAUTHORIZED);
    }

    @ExceptionHandler(ZendeskAssistanceInvalidUserEmailException.class)
    public ResponseEntity<ErrorDTO> handleZendeskAssistanceInvalidUserEmailException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.ErrorEnum.INVALID_EMAIL);
    }

    @ExceptionHandler(BizEventsPaidNoticeNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleBizEventsNoticeNotFoundException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.NOT_FOUND, ErrorDTO.ErrorEnum.NOTICE_NOT_FOUND_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.ErrorEnum.GENERIC_ERROR);
    }

    @ExceptionHandler(GPDInvalidRequestException.class)
    public ResponseEntity<ErrorDTO> handleGPDInvalidRequestException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.ErrorEnum.INVALID_REQUEST);
    }

    @ExceptionHandler(GPDPaymentNoticeDetailsNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleGPDNotFoundException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.NOT_FOUND, ErrorDTO.ErrorEnum.PAYMENT_NOTICE_NOT_FOUND_ERROR);
    }

    @ExceptionHandler(GPDInvocationException.class)
    public ResponseEntity<ErrorDTO> handleGPDInvocationException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.ErrorEnum.GENERIC_ERROR);
    }

    @ExceptionHandler(GPDTooManyRequestException.class)
    public ResponseEntity<ErrorDTO> handleGPDTooManyRequestException(RuntimeException ex, HttpServletRequest request){
        return handleArcErrorException(ex, request, HttpStatus.TOO_MANY_REQUESTS, ErrorDTO.ErrorEnum.TOO_MANY_REQUEST);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ErrorDTO> handleHttpClientErrorException(HttpClientErrorException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        return handleArcErrorException(ex, request, status!=null?status:HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.ErrorEnum.GENERIC_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleViolationException(ValidationException ex, HttpServletRequest request) {
        return handleArcErrorException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.ErrorEnum.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return handleArcErrorException(ex, request, HttpStatus.NOT_FOUND, ErrorDTO.ErrorEnum.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return String.format("[%s]: %s", fieldName, errorMessage);
                }).collect(Collectors.joining("; "));

        log.info("A MethodArgumentNotValidException occurred handling request {}: HttpStatus 400 - {}", request.getMethod(), request.getRequestURI());
        log.debug(VALIDATION_ERROR_DEBUG_MESSAGE, ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ErrorDTO(ErrorDTO.ErrorEnum.INVALID_REQUEST, message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintExceptions(ConstraintViolationException ex, HttpServletRequest request) {

        log.info("A ConstraintViolationException occurred handling request {}: HttpStatus 400 - {}", request.getMethod(), request.getRequestURI());
        log.debug(VALIDATION_ERROR_DEBUG_MESSAGE, ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ErrorDTO(ErrorDTO.ErrorEnum.INVALID_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        log.info("A MethodArgumentTypeMismatchException occurred handling request {}: HttpStatus 400 - {}", request.getMethod(), request.getRequestURI());
        log.debug(VALIDATION_ERROR_DEBUG_MESSAGE, ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ErrorDTO(ErrorDTO.ErrorEnum.INVALID_REQUEST, ex.getMessage()));
    }


    private static ResponseEntity<ErrorDTO> handleArcErrorException(RuntimeException ex, HttpServletRequest request, HttpStatus httpStatus, ErrorDTO.ErrorEnum errorEnum) {
        String message = ex.getMessage();
        log.error("A {} occurred handling request {}: HttpStatus {} - {}",
                ex.getClass(),
                getRequestDetails(request),
                httpStatus.value(),
                message,
                ex);

        return ResponseEntity
                .status(httpStatus)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new ErrorDTO(errorEnum, message));
    }

    private static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }
}
