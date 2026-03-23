package it.gov.pagopa.arc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import it.gov.pagopa.arc.connector.external.googlerecaptcha.GoogleRecaptchaService;
import it.gov.pagopa.arc.model.generated.ErrorDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@ConditionalOnProperty(name = "google-recaptcha.enabled", havingValue = "true")
public class RecaptchaFilter extends OncePerRequestFilter {
    private final List<String> recaptchaProtectedPaths;
    public static final String X_RECAPTCHA_TOKEN = "X-recaptcha-token";
    public static final String MISSING_RECAPTCHA_HEADER_ERROR_CODE = "MISSING_RECAPTCHA_HEADER";
    private final GoogleRecaptchaService googleRecaptchaService;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public RecaptchaFilter(@Value("${google-recaptcha.protected-paths}") List<String> recaptchaProtectedPaths, GoogleRecaptchaService googleRecaptchaService, ObjectMapper objectMapper) {
        this.recaptchaProtectedPaths = recaptchaProtectedPaths;
        this.googleRecaptchaService = googleRecaptchaService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return recaptchaProtectedPaths.stream()
                .noneMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader(X_RECAPTCHA_TOKEN);
        if (StringUtils.isBlank(token) || !googleRecaptchaService.isVerified(token)) {
            setErrorResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setErrorResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                ErrorDTO.builder()
                        .category(ErrorDTO.CategoryEnum.UNAUTHORIZED)
                        .message("Invalid or missing reCAPTCHA token")
                        .code(MISSING_RECAPTCHA_HEADER_ERROR_CODE)
                        .build()));
    }
}