package tw.com.fcb.mimosa.ext.security;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;
import static tw.com.fcb.mimosa.http.APIResponse.error;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.security.autoconfigure.MimosaSecurityProperties;
import tw.com.fcb.mimosa.http.APIErrorDetail;
import tw.com.fcb.mimosa.web.APIResponseHandler;

/** @author Matt Ho */
@Slf4j
@RequiredArgsConstructor
public class ExceptionAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

  final MimosaSecurityProperties properties;
  final AbstractJackson2HttpMessageConverter messageConverter;
  @Autowired
  APIResponseHandler responseHandler;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {
    var outputMessage = new ServletServerHttpResponse(response);
    outputMessage.getHeaders().setAccessControlAllowOrigin("*");
    outputMessage.getHeaders().set(WWW_AUTHENTICATE, "Basic realm=" + properties.getRealm());
    var cause = ofNullable(request.getAttribute(AUTHENTICATION_EXCEPTION))
        .filter(Throwable.class::isInstance)
        .map(Throwable.class::cast)
        .orElse(authException);
    var error = error(
        err -> {
          err.code("MD-S-004-00").message("非法請求");
          if (cause != null) {
            err.detail(
                APIErrorDetail.builder()
                    .code(UNAUTHORIZED.getReasonPhrase())
                    .message(getMessage(cause))
                    .build());
          }
          return err;
        });
    messageConverter.write(
        responseHandler.apply(error, false), error.getClass(), APPLICATION_JSON, outputMessage);
  }

  @Override
  public void afterPropertiesSet() {
    setRealmName(properties.getRealm());
    super.afterPropertiesSet();
  }
}
