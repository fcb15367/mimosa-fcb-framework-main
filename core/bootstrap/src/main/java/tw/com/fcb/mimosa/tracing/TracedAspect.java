package tw.com.fcb.mimosa.tracing;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_OBJECT_ARRAY;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static tw.com.fcb.mimosa.tracing.TraceManager.traced;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.opentracing.tag.Tags;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** @author Matt Ho */
@Aspect
@Slf4j
@RequiredArgsConstructor
public class TracedAspect {

  @NonNull
  final TracingProperties.Traced properties;
  @NonNull
  final ObjectMapper mapper;

  @Around("@annotation(traced) || @within(traced)")
  public Object trace(final ProceedingJoinPoint pjp, Traced traced) throws Throwable {
    if (traced == null) {
      traced = getAnnotationFromTargetMethod(pjp);
    }
    var name = createTaskName(pjp, traced);
    var root = !TraceManager.isActive();
    if (root) {
      TraceManager.init(name, properties.getTimeUnit());
    }
    try (var ignored = traced(name, traced.buildSpan(), buildTags(pjp, traced))) {
      return pjp.proceed();
    } finally {
      if (root) {
        TraceManager.get().map(properties.getStyle()::format).ifPresent(log::info);
        TraceManager.clear();
      }
    }
  }

  private Traced getAnnotationFromTargetMethod(ProceedingJoinPoint pjp)
      throws NoSuchMethodException {
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    Method method = pjp.getTarget()
        .getClass()
        .getMethod(signature.getMethod().getName(), signature.getMethod().getParameterTypes());
    return method.getAnnotation(Traced.class);
  }

  private Map<String, String> buildTags(ProceedingJoinPoint pjp, Traced traced) {
    if (!traced.buildSpan()) {
      return emptyMap();
    }
    var tags = Arrays.stream(traced.tags())
        .filter(tag -> tag.contains("="))
        .map(tag -> tag.split("="))
        .collect(toMap(v -> v[0], v -> v[1]));
    try {
      tags.put(Tags.COMPONENT.getKey(), properties.getComponent());
      tags.put("joinpoint", pjp.toString());
      tags.put("joinpoint.kind", pjp.getKind());
      tags.put("joinpoint.target.class", pjp.getTarget().getClass().getName());
      tags.put("joinpoint.signature.method", pjp.getSignature().getName());
      if (properties.isArguments()) {
        var tag = "joinpoint.args";
        var value = ofNullable(pjp.getArgs()).orElse(EMPTY_OBJECT_ARRAY);
        try {
          tags.put(tag, mapper.writeValueAsString(value));
        } catch (Exception e) {
          log.warn(
              "Failed parsing json for args of '{}', fallback using to-string!",
              pjp.toLongString(),
              e);
          tags.put(tag, Objects.toString(value));
        }
      }
    } catch (Exception e) {
      log.warn("Error building tags for opentracing", e);
      tags.put("build-tags.error", getRootCauseMessage(e));
    }
    return tags;
  }

  protected String createTaskName(@NonNull ProceedingJoinPoint pjp, @Nullable Traced traced) {
    return ofNullable(traced)
        .map(Traced::name)
        .filter(StringUtils::hasText)
        .orElseGet(
            () -> {
              var targetClass = pjp.getTarget().getClass().getName();
              var targetMethod = pjp.getSignature().getName();
              return format("%s.%s", targetClass, targetMethod);
            });
  }
}
