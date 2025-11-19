package it.gov.pagopa.arc.utils;

import it.gov.pagopa.arc.dto.IamUserInfoDTO;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;

public final class SecurityUtils {

  private SecurityUtils(){}

  public static IamUserInfoDTO getPrincipal() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if(principal instanceof IamUserInfoDTO loggedUser){
        return loggedUser;
    }else if(principal instanceof String){
        return null;
    }else{
        throw new IllegalStateException("Invalid principal type: expected IamUserInfoDTO but got " + principal.getClass().getName());
    }
  }

  public static String getUserFiscalCode() {
    IamUserInfoDTO principal = getPrincipal();

    if (principal.getFiscalCode() == null) {
      throw new IllegalArgumentException("Fiscal code is missing for the authenticated user");
    }

    return principal.getFiscalCode();
  }

  public static String getUserId() {
    IamUserInfoDTO principal = getPrincipal();

    if (principal.getUserId() == null) {
      throw new IllegalArgumentException("User id is missing for the authenticated user");
    }

    return principal.getUserId();
  }

  public static String removePiiFromURI(URI uri){
    return uri != null
            ? uri.toString().replaceAll("=[^&]*", "=***")
            : null;
  }

}
