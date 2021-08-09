package tw.com.fcb.mimosa.ext.security.autoconfigure;

import static java.util.Optional.ofNullable;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.opentracing.contrib.java.spring.jaeger.starter.JaegerConfigurationProperties;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.ext.security.BaggageRequestHeaderAuthenticationFilter;
import tw.com.fcb.mimosa.ext.security.PreAuthenticatedPropagation;
import tw.com.fcb.mimosa.ext.security.PreAuthenticatedScopeInterceptor;
import tw.com.fcb.mimosa.tracing.ScopeInterceptor.ScopeInterceptorBuilder;

/** @author Matt Ho */
@RequiredArgsConstructor
class FilterConfiguration {

  final MimosaSecurityProperties security;
  final JaegerConfigurationProperties jaeger;
  final AuthenticationProvider authenticationProvider;
  final ObjectMapper mapper;

  @Bean
  @ConditionalOnMissingBean
  RequestHeaderAuthenticationFilter preAuthenticationFilter() {
    var filter = new BaggageRequestHeaderAuthenticationFilter(
        security.getHeader().toBaggage(mapper), jaeger);
    filter.setPrincipalRequestHeader(security.getHeader().getPrincipal());
    ofNullable(security.getHeader().getCredentials())
        .ifPresent(filter::setCredentialsRequestHeader);
    filter.setAuthenticationManager(new ProviderManager(authenticationProvider));
    filter.setExceptionIfHeaderMissing(false);
    filter.setAuthenticationSuccessHandler(
        new PreAuthenticatedPropagation(security.getHeader().toBaggage(mapper)));
    return filter;
  }

  @Bean
  ScopeInterceptorBuilder preAuthenticatedScopeLifecycle() {
    return () -> new PreAuthenticatedScopeInterceptor(
        new ProviderManager(authenticationProvider), security.getHeader().toBaggage(mapper));
  }
}
