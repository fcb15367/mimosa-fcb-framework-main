package tw.com.fcb.mimosa.ext.security;

import static java.util.Optional.ofNullable;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.ext.security.autoconfigure.MimosaSecurityProperties;

/** @author Matt Ho */
@Order(99) // WebSecurityConfigurerAdapter 預設 100, 所以我們 99
@RequiredArgsConstructor
public class PreAuthenticatedWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @NonNull
  final MimosaSecurityProperties properties;
  @NonNull
  final RequestHeaderAuthenticationFilter preAuthenticatedFilter;
  @NonNull
  final AuthenticationEntryPoint authenticationEntryPoint;

  @Override
  public void configure(WebSecurity web) {
    properties.getIgnoring().stream()
        .map(this::antMatchers)
        .forEach(web.ignoring()::requestMatchers);
  }

  RequestMatcher[] antMatchers(MimosaSecurityProperties.Ignoring ignoring) {
    return ignoring.getPath().stream()
        .map(
            path -> new AntPathRequestMatcher(
                path, ofNullable(ignoring.getMethod()).map(HttpMethod::toString).orElse(null)))
        .toArray(RequestMatcher[]::new);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.authorizeRequests().anyRequest().authenticated();
    http.addFilterAfter(preAuthenticatedFilter, RequestHeaderAuthenticationFilter.class);
    http.sessionManagement().sessionCreationPolicy(STATELESS);
    http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
  }
}
