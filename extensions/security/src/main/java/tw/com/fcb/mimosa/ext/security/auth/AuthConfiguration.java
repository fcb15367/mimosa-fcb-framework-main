package tw.com.fcb.mimosa.ext.security.auth;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.autoconfigure.MimosaSecurityProperties;

/** @author Matt Ho */
@Slf4j
@RequiredArgsConstructor
public class AuthConfiguration {

  final MimosaSecurityProperties properties;

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty("mimosa.security.backend-auth-service.url")
  public AuthClient restAuthClient() {
    log.info(
        "Creating RestAuthClient for endpoint: {}", properties.getBackendAuthService().getUrl());
    return new RestAuthClient(properties.getBackendAuthService().getUrl());
  }

  @Bean
  @ConditionalOnMissingBean
  public AuthClient noOpAuthClient() {
    log.info("Creating NoOpAuthClient because 'mimosa.validation.url' is empty");
    return new NoOpAuthClient();
  }
}
