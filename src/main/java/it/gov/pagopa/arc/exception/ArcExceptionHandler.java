package it.gov.pagopa.arc.exception;

import it.gov.pagopa.arc.exception.common.CommonExceptionHandler;
import it.gov.pagopa.arc.exception.custom.InvalidTokenException;
import it.gov.pagopa.arc.exception.custom.ZendeskAssistanceInvalidUserEmailException;
import it.gov.pagopa.arc.model.generated.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ArcExceptionHandler extends CommonExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDTO> handlePullInvalidTokenException(InvalidTokenException ex, HttpServletRequest request){
        return handleException(ex, request, HttpStatus.UNAUTHORIZED, ErrorDTO.CategoryEnum.UNAUTHORIZED);
    }

    @ExceptionHandler(ZendeskAssistanceInvalidUserEmailException.class)
    public ResponseEntity<ErrorDTO> handleZendeskAssistanceInvalidUserEmailException(RuntimeException ex, HttpServletRequest request){
        return handleException(ex, request, HttpStatus.BAD_REQUEST, ErrorDTO.CategoryEnum.BAD_REQUEST);
    }

}
