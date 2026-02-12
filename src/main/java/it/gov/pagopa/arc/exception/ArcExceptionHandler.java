package it.gov.pagopa.arc.exception;

import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.arc.dto.mapper.UpstreamErrorMapper;
import it.gov.pagopa.arc.exception.custom.BaseBusinessException;
import it.gov.pagopa.arc.exception.custom.InvalidTokenException;
import it.gov.pagopa.arc.exception.custom.ResourceNotFoundException;
import it.gov.pagopa.arc.exception.custom.ZendeskAssistanceInvalidUserEmailException;
import it.gov.pagopa.arc.model.generated.ErrorDTO;
import it.gov.pagopa.arc.utils.ErrorMessageParser;
import it.gov.pagopa.arc.utils.Utilities;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ArcExceptionHandler {

    private final UpstreamErrorMapper upstreamErrorMapper;

    public ArcExceptionHandler(UpstreamErrorMapper upstreamErrorMapper) {
        this.upstreamErrorMapper = upstreamErrorMapper;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDTO> handlePullInvalidTokenException(RuntimeException ex, HttpServletRequest request){
        return handleException(ex, request, HttpStatus.UNAUTHORIZED, ErrorDTO.CategoryEnum.UNAUTHORIZED);
    }

    @ExceptionHandler(ZendeskAssistanceInvalidUserEmailException.class)
    public ResponseEntity<ErrorDTO> handleZendeskAssistanceInvalidUserEmailException(RuntimeException ex, HttpServletRequest request){
        return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDTO> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request){
        return handleException(ex, request, HttpStatus.INTERNAL_SERVER_ERROR, ErrorDTO.CategoryEnum.GENERIC_ERROR);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<ErrorDTO> handleHttpClientErrorException(HttpClientErrorException ex, HttpServletRequest request) {
        logException(ex, request, ex.getStatusCode());

        ErrorDTO.CategoryEnum category = transcodeStatus(ex.getStatusCode());
        String traceId = Utilities.getTraceId();
        String message = ex.getMessage();
        String code = "GENERIC_ERROR";

        UpstreamErrorMapper.MappedUpstreamError mapped = upstreamErrorMapper.from(ex);
        if(mapped != null) {
            message = mapped.description();
            code = mapped.code();
        }

        ErrorDTO dto = new ErrorDTO();
        dto.setCategory(category);
        dto.setMessage(message);
        dto.setTraceId(traceId);
        dto.setCode(code);

        return ResponseEntity
                .status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    private static ErrorDTO.CategoryEnum transcodeStatus(HttpStatusCode status) {
        if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) return ErrorDTO.CategoryEnum.NOT_FOUND;
        if (status.isSameCodeAs(HttpStatus.CONFLICT)) return ErrorDTO.CategoryEnum.CONFLICT;
        if (status.isSameCodeAs(HttpStatus.FORBIDDEN)) return ErrorDTO.CategoryEnum.FORBIDDEN;
        if (status.is4xxClientError()) return ErrorDTO.CategoryEnum.BAD_REQUEST;
        return ErrorDTO.CategoryEnum.GENERIC_ERROR;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.NOT_FOUND, ErrorDTO.CategoryEnum.NOT_FOUND);
    }

    @ExceptionHandler({ValidationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class, MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorDTO> handleViolationException(Exception ex, HttpServletRequest request) {
        return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
    }

    static ResponseEntity<ErrorDTO> handleException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus, ErrorDTO.CategoryEnum category) {
        logException(ex, request, httpStatus);

        String message = buildReturnedMessage(ex);
        String code;

        if (ex instanceof BaseBusinessException codedEx && StringUtils.isNotBlank(codedEx.getCode())) {
            code = codedEx.getCode();
        } else {
            ErrorMessageParser.ParsedError parsed = ErrorMessageParser.parse(message);
            code = parsed.code();
            if (parsed.description() != null) {
                message = parsed.description();
            }
        }

        ErrorDTO dto = new ErrorDTO();
        dto.setCategory(category);
        dto.setMessage(message);
        dto.setTraceId(Utilities.getTraceId());
        dto.setCode(code);

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto);
    }

    private static void logException(Exception ex, HttpServletRequest request, HttpStatusCode httpStatus) {
        boolean printStackTrace = httpStatus.is5xxServerError();
        Level logLevel = printStackTrace ? Level.ERROR : Level.INFO;
        log.makeLoggingEventBuilder(logLevel)
                .log("A {} occurred handling request {}: HttpStatus {} - {}",
                        ex.getClass(),
                        getRequestDetails(request),
                        httpStatus.value(),
                        ex.getMessage(),
                        printStackTrace ? ex : null
                );
        if (!printStackTrace && log.isDebugEnabled() && ex.getCause() != null) {
            log.debug("CausedBy: ", ex.getCause());
        }
    }

    private static String buildReturnedMessage(Exception ex) {
        switch (ex) {
            case HttpMessageNotReadableException httpMessageNotReadableException -> {
                if (httpMessageNotReadableException.getCause() instanceof DatabindException jsonMappingException) {
                    return "Cannot parse body. " +
                            jsonMappingException.getPath().stream()
                                    .map(JacksonException.Reference::getPropertyName)
                                    .collect(Collectors.joining(".")) +
                            ": " + jsonMappingException.getOriginalMessage();
                }
                return "Required request body is missing";
            }
            case MethodArgumentNotValidException methodArgumentNotValidException -> {
                return "Invalid request content." +
                        methodArgumentNotValidException.getBindingResult()
                                .getAllErrors().stream()
                                .map(e -> " " +
                                        (e instanceof FieldError fieldError ? fieldError.getField() : e.getObjectName()) +
                                        ": " + e.getDefaultMessage())
                                .sorted()
                                .collect(Collectors.joining(";"));
            }
            case ConstraintViolationException constraintViolationException -> {
                return "Invalid request content." +
                        constraintViolationException.getConstraintViolations()
                                .stream()
                                .map(e -> " " + e.getPropertyPath() + ": " + e.getMessage())
                                .sorted()
                                .collect(Collectors.joining(";"));
            }
            default -> {
                return ex.getMessage();
            }
        }
    }

    private static String getRequestDetails(HttpServletRequest request) {
        return "%s %s".formatted(request.getMethod(), request.getRequestURI());
    }
}
