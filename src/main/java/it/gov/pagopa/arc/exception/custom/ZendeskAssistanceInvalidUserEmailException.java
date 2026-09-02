package it.gov.pagopa.arc.exception.custom;

import it.gov.pagopa.arc.exception.common.BaseBusinessException;
import lombok.Getter;

@Getter
public class ZendeskAssistanceInvalidUserEmailException extends BaseBusinessException {

    public ZendeskAssistanceInvalidUserEmailException(String message) {
        super("INVALID_ZENDESK_USER", message);
    }
}
