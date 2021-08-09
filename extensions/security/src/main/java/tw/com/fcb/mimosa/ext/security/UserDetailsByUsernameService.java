package tw.com.fcb.mimosa.ext.security;

import static java.lang.String.format;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static tw.com.fcb.mimosa.ext.security.SimpleUser.ANONYMOUS;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.auth.AuthClient;
import tw.com.fcb.mimosa.ext.security.auth.AuthMapper;
import tw.com.fcb.mimosa.ext.security.auth.ValidateRequest;

/** @author Matt Ho */
@Slf4j
@RequiredArgsConstructor
public class UserDetailsByUsernameService implements UserDetailsService {

  static final Pattern BEARER_AUTH = Pattern.compile("^Bearer\\s+(.+)$");

  final ObjectMapper mapper;
  final boolean strict;

  @Autowired
  AuthClient authClient;
  @Autowired
  AuthMapper authMapper;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var matcher = BEARER_AUTH.matcher(username);
    if (matcher.find()) {
      log.debug("Found Bearer Authentication token: {}", username);
      return loadUserByBearerToken(matcher.group(1));
    }
    log.debug("Not found Bearer Authentication token: {}", username);
    return loadUserByJson(username);
  }

  private UserDetails loadUserByBearerToken(String token) {
    try {

      return decodeToken(token)
          .filter(UserDetails::isEnabled)
          .map((t) -> {
            if (strict)
              return t;
            log.debug("SimpleUser.pass strict mode: {} forcePass", strict);
            return t.pass();
          })
          .orElseGet(this::defaultUserDetails);
    } catch (AuthenticationException ae) {
      log.error("Unable to load user by bearer token: [{}]", token, ae);
      throw ae;
    } catch (Exception e) {
      log.error("Unable to load user by bearer token: [{}]", token, e);
      throw new InternalAuthenticationServiceException(
          format("Unable to load user by bearer token (%s)", getRootCauseMessage(e)), e);
    }
  }

  private UserDetails loadUserByJson(String json) {
    try {
      return decodeJson(json)
          .map((t) -> {
            if (strict)
              return t;
            log.debug("SimpleUser.pass strict mode: {} forcePass", strict);
            return t.pass();
          })
          .orElseGet(this::defaultUserDetails);
    } catch (AuthenticationException ae) {
      log.error("Unable to load user by json: [{}]", json, ae);
      throw ae;
    } catch (Exception e) {
      log.error("Unable to load user by json: [{}]", json, e);
      throw new InternalAuthenticationServiceException(
          format("Unable to load user by json (%s)", getRootCauseMessage(e)), e);
    }
  }

  private UserDetails defaultUserDetails() {
    if (strict) {
      throw new UsernameNotFoundException(
          "Bearer Authentication is required in security strict mode");
    }
    return ANONYMOUS;
  }

  Optional<SimpleUser> decodeJson(@NonNull String json) {
    try {
      return Optional.of(mapper.readValue(json, SimpleUser.class));
    } catch (JsonProcessingException e) {
      log.debug("Failed to decode json to SimpleUser: {}", json, e);
      return Optional.empty();
    }
  }

  Optional<SimpleUser> decodeToken(@NonNull String token) throws AuthenticationServiceException {
    try {
      // TODO: call auth-client and map response to simple-user
      return Optional.of(
          authMapper.toSimpleUser(
              authClient.validateToken(ValidateRequest.builder().token(token).build())));
    } catch (Exception e) {
      log.error("Failed to call backend auth service for token: {}", token, e);
      throw new AuthenticationServiceException(
          format("Failed to call backend auth service (%s)", getRootCauseMessage(e)), e);
    }
  }
}
