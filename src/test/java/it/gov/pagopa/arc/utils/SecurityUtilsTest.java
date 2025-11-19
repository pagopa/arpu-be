package it.gov.pagopa.arc.utils;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import it.gov.pagopa.arc.fakers.auth.IamUserInfoDTOFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.net.URI;

public class SecurityUtilsTest {

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  public static void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  public static void configureSecurityContext(IamUserInfoDTO userInfo) {
    configureSecurityContext("TOKENHEADER.TOKENPAYLOAD.TOKENDIGEST", userInfo);
  }

  public static void configureSecurityContext(String token, IamUserInfoDTO userInfo) {
    SecurityContextHolder.setContext(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(userInfo, token)));
  }

  @Test
  void givenConfiguredSecurityContextThenRetrieveTheAuthenticatedUser(){
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            IamUserInfoDTOFaker.mockInstance(), null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    IamUserInfoDTO user = SecurityUtils.getPrincipal();
    Assertions.assertNotNull(user);
  }

  @Test
  void givenWrongConfiguredSecurityContextThenThrowException(){
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            new Object(), null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class, SecurityUtils::getPrincipal);
    Assertions.assertEquals("Invalid principal type: expected IamUserInfoDTO but got java.lang.Object", ex.getMessage());
  }

  @Test
  void givenDefaultAnonymousConfiguredSecurityContextThenThrowException(){
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            "anonymousUser", null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    IamUserInfoDTO user = SecurityUtils.getPrincipal();
    Assertions.assertNull(user);
  }

  @Test
  void givenConfiguredSecurityContextWhenGetUserFiscalCodeThenReturnAuthenticatedUserFiscalCode(){
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            IamUserInfoDTOFaker.mockInstance(), null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String userFiscalCode = SecurityUtils.getUserFiscalCode();
    Assertions.assertNotNull(userFiscalCode);
    Assertions.assertEquals("FISCAL-CODE789456", userFiscalCode);
  }

  @Test
  void givenPrincipalNullFiscalCodeWhenGetUserFiscalCodeThenThrowException(){
    IamUserInfoDTO iamUserInfoDTO = IamUserInfoDTOFaker.mockInstance();
    iamUserInfoDTO.setFiscalCode(null);

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
           iamUserInfoDTO , null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, SecurityUtils::getUserFiscalCode);
    Assertions.assertEquals("Fiscal code is missing for the authenticated user", ex.getMessage());
  }

  @Test
  void givenConfiguredSecurityContextWhenGetUserIdeThenReturnAuthenticatedUserId(){
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            IamUserInfoDTOFaker.mockInstance(), null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String userId = SecurityUtils.getUserId();
    Assertions.assertNotNull(userId);
    Assertions.assertEquals("user_id",  userId);
  }

  @Test
  void givenPrincipalNullUserIdWhenGetUserIdThenThrowException(){
    IamUserInfoDTO iamUserInfoDTO = IamUserInfoDTOFaker.mockInstance();
    iamUserInfoDTO.setUserId(null);

    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            iamUserInfoDTO , null, null);
    authentication.setDetails(new WebAuthenticationDetails(new MockHttpServletRequest()));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, SecurityUtils::getUserId);
    Assertions.assertEquals("User id is missing for the authenticated user", ex.getMessage());
  }

  @Test
  void givenUriWhenRemovePiiFromURIThenOk(){
    String result = SecurityUtils.removePiiFromURI(URI.create("https://host/path?param1=PII&param2=noPII"));
    Assertions.assertEquals("https://host/path?param1=***&param2=***", result);
  }

  @Test
  void givenNullUriWhenRemovePiiFromURIThenOk(){
    Assertions.assertNull(SecurityUtils.removePiiFromURI(null));
  }

}