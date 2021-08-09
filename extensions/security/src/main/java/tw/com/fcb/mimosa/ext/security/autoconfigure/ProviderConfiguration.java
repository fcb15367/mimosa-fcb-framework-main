package tw.com.fcb.mimosa.ext.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.ext.security.UserDetailsByUsernameService;

/** @author Matt Ho */
@RequiredArgsConstructor
class ProviderConfiguration {

  final UserDetailsByUsernameService userDetailsService;

  @Bean
  @ConditionalOnMissingBean
  AuthenticationProvider authenticationProvider() {
    var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
    authenticationProvider.setPreAuthenticatedUserDetailsService(
        new UserDetailsByNameServiceWrapper<>(userDetailsService));
    authenticationProvider.setThrowExceptionWhenTokenRejected(false);
    return authenticationProvider;
  }
}
