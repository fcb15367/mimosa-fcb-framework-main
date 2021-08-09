package tw.com.fcb.mimosa.ext.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.ext.security.PreAuthenticatedWebSecurityConfiguration;

/** @author Matt Ho */
@RequiredArgsConstructor
class WebSecurityConfiguration {

  final MimosaSecurityProperties properties;
  final RequestHeaderAuthenticationFilter authenticationFilter;
  final AuthenticationEntryPoint authenticationEntryPoint;

  @Bean
  @ConditionalOnMissingBean
  PreAuthenticatedWebSecurityConfiguration webSecurityConfiguration() {
    return new PreAuthenticatedWebSecurityConfiguration(
        properties, authenticationFilter, authenticationEntryPoint);
  }
}
