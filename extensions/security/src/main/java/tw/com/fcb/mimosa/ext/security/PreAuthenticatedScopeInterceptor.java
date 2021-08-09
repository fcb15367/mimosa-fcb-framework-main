package tw.com.fcb.mimosa.ext.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.autoconfigure.MimosaSecurityProperties;
import tw.com.fcb.mimosa.tracing.ScopeInterceptor;

/** @author Matt Ho */
@Slf4j
@RequiredArgsConstructor
public class PreAuthenticatedScopeInterceptor implements ScopeInterceptor {

  @NonNull
  final AuthenticationManager authenticationManager;
  @NonNull
  final MimosaSecurityProperties.Baggage baggage;

  private Authentication previousAuthentication;

  @Override
  public void preHandle(Scope scope, Span activeSpan) {
    this.previousAuthentication = SecurityContextHolder.getContext().getAuthentication();
    doAuthenticate(activeSpan);
  }

  @Override
  public void postHandle(Scope scope, Span activeSpan) {
    SecurityContextHolder.getContext().setAuthentication(previousAuthentication);
  }

  void doAuthenticate(Span span) {
    var baggagePrincipal = baggage.getPrincipalFromSpan(span);
    if (baggagePrincipal.isEmpty()) {
      log.trace(
          "No pre-authenticated principal found in jaeger-span-context for key: {}",
          baggage.getPrincipalKey());
      return;
    }
    var principal = baggagePrincipal.get();
    var credentials = baggage.getCredentialsFromSpan(span).orElse("N/A");
    log.debug(
        "Found pre-authenticated principal = {}, credentials = {}, trying to authenticate",
        principal,
        credentials);
    try {
      var token = new PreAuthenticatedAuthenticationToken(principal, credentials);
      var authenticated = authenticationManager.authenticate(token);
      log.debug("Authentication success: " + authenticated);
      SecurityContextHolder.getContext().setAuthentication(authenticated);
    } catch (AuthenticationException ignored) {
      // 這邊不處理, 讓之後的 spring security filter 去處理吧
      log.debug("Failed to authenticate, ignoring the exception...", ignored);
    }
  }
}
