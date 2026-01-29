package it.gov.pagopa.arc.utils;


import it.gov.pagopa.arc.exception.custom.ZendeskAssistanceInvalidUserEmailException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

@Slf4j
public class Utilities {
    private Utilities(){}

    /**
     * To extract name value from email
     */
    public static String extractNameFromEmailAssistanceToken(String userMail){
        String nameExtracted;

        if(!StringUtils.isBlank(userMail) && userMail.contains("@")){
            int index = userMail.indexOf("@");
            nameExtracted = userMail.substring(0, index);
        }else {
            throw new ZendeskAssistanceInvalidUserEmailException("Invalid user email [%s]".formatted(userMail));
        }
        return  nameExtracted;
    }

    public static String getTraceId(){
        return MDC.get("traceId");
    }
}
