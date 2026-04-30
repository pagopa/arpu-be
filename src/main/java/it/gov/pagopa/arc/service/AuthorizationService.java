package it.gov.pagopa.arc.service;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;

public class AuthorizationService {
    private AuthorizationService(){}

    public static String getDebtorFiscalCode(String fiscalCode, IamUserInfoDTO loggedUser){
        if(StringUtils.isNotBlank(fiscalCode)){
            return fiscalCode;
        }
        if(loggedUser!=null && StringUtils.isNotBlank(loggedUser.getFiscalCode())){
            return loggedUser.getFiscalCode();
        }
        throw new ValidationException("[MISSING_FISCAL_CODE] Either the fiscalCode must not be null or the user must be logged");
    }
}
