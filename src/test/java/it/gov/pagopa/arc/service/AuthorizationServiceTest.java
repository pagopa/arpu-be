package it.gov.pagopa.arc.service;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.utils.TestUtils;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;

class AuthorizationServiceTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Test
    void givenFiscalCodeWhenGetDebtorFiscalCodeThenOk(){
        String fiscalCode = "fiscalCode";
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

        String result = AuthorizationService.getDebtorFiscalCode(fiscalCode,loggedUser);

        Assertions.assertEquals(fiscalCode,result);
    }

    @Test
    void givenNoFiscalCodeWhenGetDebtorFiscalCodeThenOk(){
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);

        String result = AuthorizationService.getDebtorFiscalCode(null,loggedUser);

        Assertions.assertEquals(loggedUser.getFiscalCode(),result);
    }

    @Test
    void givenNoFiscalCodeAndNoLoggedUserFiscalCodeWhenGetDebtorFiscalCodeThenValidationException(){
        IamUserInfoDTO loggedUser = podamFactory.manufacturePojo(IamUserInfoDTO.class);
        loggedUser.setFiscalCode(null);

        Assertions.assertThrows(ValidationException.class,()->AuthorizationService.getDebtorFiscalCode(null,loggedUser));
    }
}
