package tw.com.fcb.mimosa.web;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes;
import static org.springframework.web.util.WebUtils.getNativeRequest;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.http.APIResponse;

@Slf4j
@Aspect
@RequiredArgsConstructor
class APIResponseAspect {

  private static final String UNKNOWN_PAYLOAD = "[unknown-payload]";
  private static final String UNKNOWN_HOST = "[unknown-host]";

  final APIResponseHandler responseHandler;
  final ObjectMapper mapper;

  @Pointcut("within(@(@org.springframework.stereotype.Controller *) *)")
  void controller() {
  }

  @Around("controller()")
  Object around(ProceedingJoinPoint pjp) throws Throwable {
    var signature = (MethodSignature) pjp.getSignature();
    if (!APIResponse.class.isAssignableFrom(signature.getReturnType())) {
      return pjp.proceed();
    }

    responseHandler.validateStrict();

    var mdc = new HashMap<String, String>();
    // TODO 這邊還有些欄位沒印
    var now = LocalDateTime.now();
    mdc.put("logDateTime", now.toString());
    mdc.put("logTimestamp", Timestamp.valueOf(now).toString());
    mdc.put("host", getHostName());
    mdc.put("processState", "0001");
    var request = ((ServletRequestAttributes) currentRequestAttributes()).getRequest();
    mdc.put("serviceRoot", request.getRequestURL().toString());
    mdc.put("resourcePath", request.getRequestURI());

    var payload = getMessagePayload(request).or(() -> getMethodArguments(pjp)).orElse(
        UNKNOWN_PAYLOAD);
    try (var ignore = closeable(mdc)) {
      log.info("{}", payload);
    }

    var watch = new StopWatch();
    watch.start();
    var proceed = pjp.proceed();
    watch.stop();

    now = LocalDateTime.now();
    mdc.put("logDateTime", now.toString());
    mdc.put("logTimestamp", Timestamp.valueOf(now).toString());
    mdc.put("processState", "9999");
    mdc.put("elapsedTime", "" + watch.getTotalTimeMillis());
    try (var ignore = closeable(mdc)) {
      log.info("{}", payload);
    }

    return responseHandler.apply((APIResponse<?>) proceed);
  }

  private AutoCloseable closeable(@NonNull Map<String, String> mdc) {
    mdc.forEach(MDC::put);
    return () -> mdc.keySet().forEach(MDC::remove);
  }

  private String getHostName() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      log.debug("failed to get hostname: {}", getRootCauseMessage(e));
      return UNKNOWN_HOST;
    }
  }

  protected Optional<String> getMethodArguments(ProceedingJoinPoint pjp) {
    try {
      return of(mapper.writeValueAsString(pjp.getArgs()));
    } catch (JsonProcessingException e) {
      log.debug("failed to write pjp.args to json: {}", getRootCauseMessage(e));
      return empty();
    }
  }

  private Optional<String> getMessagePayload(HttpServletRequest request) {
    return ofNullable(getNativeRequest(request, ContentCachingRequestWrapper.class))
        .map(
            wrapper -> {
              byte[] buf = wrapper.getContentAsByteArray();
              if (buf.length > 0) {
                try {
                  return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException ex) {
                  log.debug("failed to get message payload: {}", getRootCauseMessage(ex));
                }
              }
              return null;
            });
  }
}
