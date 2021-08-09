package tw.com.fcb.mimosa.ext.security;

import static java.util.Optional.ofNullable;
import static tw.com.fcb.mimosa.tracing.TraceContext.getActiveSpan;

import java.util.Optional;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import io.opentracing.contrib.java.spring.jaeger.starter.JaegerConfigurationProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.autoconfigure.MimosaSecurityProperties;

/** @author Matt Ho */
@Slf4j
@RequiredArgsConstructor
public class BaggageRequestHeaderAuthenticationFilter extends RequestHeaderAuthenticationFilter {

  @NonNull
  final MimosaSecurityProperties.Baggage baggage;
  @NonNull
  final JaegerConfigurationProperties jaeger;

  @Override
  protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    final Object principal = getPreAuthenticated(
        () -> super.getPreAuthenticatedPrincipal(request),
        () -> getActiveSpan().flatMap(baggage::getPrincipalFromSpan));
    if (log.isDebugEnabled() && jaeger.isEnableB3Propagation() && principal == null) {
      log.debug(
          "OpenTracing Jaeger 在開啟 b3-propagation 後, 在取得 baggage-item 時並沒有實作 case-insensitive, 可以先檢查 baggage principal 是否為 lowercase, request.headers={}",
          new ServletServerHttpRequest(request).getHeaders());
    }
    return principal;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return getPreAuthenticated(
        () -> super.getPreAuthenticatedCredentials(request),
        () -> getActiveSpan().flatMap(baggage::getCredentialsFromSpan));
  }

  /**
   * @param fromRequest 本次 request 有傳優先使用
   * @param fromBaggage request 沒有找到的話再檢查 baggage-item 中有沒有
   * @return
   */
  Object getPreAuthenticated(
      @NonNull Supplier<Object> fromRequest, @NonNull Supplier<Optional<String>> fromBaggage) {
    try {
      return ofNullable(fromRequest.get())
          .or(() -> fromBaggage.get().map(Object.class::cast))
          .orElse(null);
    } catch (PreAuthenticatedCredentialsNotFoundException e) {
      return fromBaggage.get().orElseThrow(() -> e);
    }
  }
}
