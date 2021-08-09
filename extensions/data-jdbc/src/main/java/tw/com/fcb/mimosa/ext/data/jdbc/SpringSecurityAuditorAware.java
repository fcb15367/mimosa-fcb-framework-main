package tw.com.fcb.mimosa.ext.data.jdbc;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/** @author Matt Ho */
public class SpringSecurityAuditorAware implements AuditorAware<String> {

  public Optional<String> getCurrentAuditor() {
    return ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .filter(UserDetails.class::isInstance)
        .map(UserDetails.class::cast)
        .map(UserDetails::getUsername);
  }
}
