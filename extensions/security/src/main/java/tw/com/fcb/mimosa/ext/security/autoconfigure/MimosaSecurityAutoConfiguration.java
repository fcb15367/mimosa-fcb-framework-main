package tw.com.fcb.mimosa.ext.security.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.ExceptionAuthenticationEntryPoint;
import tw.com.fcb.mimosa.ext.security.PermitAllWebSecurityConfiguration;
import tw.com.fcb.mimosa.ext.security.UserDetailsByUsernameService;
import tw.com.fcb.mimosa.ext.security.auth.AuthConfiguration;

@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MimosaSecurityProperties.class)
@Import({ ProviderConfiguration.class, FilterConfiguration.class, AuthConfiguration.class })
public class MimosaSecurityAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public UserDetailsByUsernameService userDetailsService(
      ObjectMapper mapper, MimosaSecurityProperties properties) {
    return new UserDetailsByUsernameService(mapper, properties.isStrict());
  }

  @Bean
  @ConditionalOnMissingBean(PreAuthenticatedConfiguration.class)
  PermitAllWebSecurityConfiguration webSecurityConfiguration() {
    return new PermitAllWebSecurityConfiguration();
  }

  @EnableWebSecurity
  @RequiredArgsConstructor
  @Configuration(proxyBeanMethods = false)
  @Import({ WebSecurityConfiguration.class })
  @ConditionalOnProperty("mimosa.security.enabled")
  static class PreAuthenticatedConfiguration {
    final MimosaSecurityProperties properties;

    @Autowired
    @Qualifier("mappingJackson2HttpMessageConverter")
    MappingJackson2HttpMessageConverter messageConverter;

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEntryPoint authenticationEntryPoint() {
      return new ExceptionAuthenticationEntryPoint(properties, messageConverter);
    }
  }
}
