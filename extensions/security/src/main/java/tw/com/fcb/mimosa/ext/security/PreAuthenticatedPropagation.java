package tw.com.fcb.mimosa.ext.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.autoconfigure.MimosaSecurityProperties;
import tw.com.fcb.mimosa.tracing.TraceContext;

/** @author Matt Ho */
@Slf4j
@RequiredArgsConstructor
public class PreAuthenticatedPropagation implements AuthenticationSuccessHandler {

  final MimosaSecurityProperties.Baggage baggage;

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    log.debug("Principal[{}]  Credentials[{}]", authentication.getPrincipal(), authentication.getCredentials());
    TraceContext.getActiveSpan()
        .ifPresent(
            span -> {
              if (span.getBaggageItem(baggage.getPrincipalKey()) == null) {
                log.debug("setPrincipalToSpan span[{}] getPrincipal[{}]", span, authentication.getPrincipal());
                baggage.setPrincipalToSpan(span, authentication.getPrincipal());
              }
              if (span.getBaggageItem(baggage.getCredentialsKey()) == null) {
                log.debug("setCredentialsToSpan span[{}] getCredentials[{}]", span, authentication.getCredentials());
                baggage.setCredentialsToSpan(span, authentication.getCredentials());
              }
            });
  }
}
