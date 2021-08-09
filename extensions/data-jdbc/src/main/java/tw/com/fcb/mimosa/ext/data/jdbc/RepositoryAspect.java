package tw.com.fcb.mimosa.ext.data.jdbc;

import static java.util.Collections.emptyList;
import static java.util.function.Predicate.not;
import static org.springframework.aop.framework.AopProxyUtils.proxiedUserInterfaces;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PreDestroy;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import tw.com.fcb.mimosa.ext.data.jdbc.event.AfterProceedCallback;
import tw.com.fcb.mimosa.ext.data.jdbc.event.AfterProceedEvent;

/**
 * 針對實作 {@link org.springframework.data.repository.Repository} 或掛 {@link
 * org.springframework.stereotype.Repository} 的程式做 ({@link Around}) AOP 攔截, 來 publish after-proceed
 * event 或 trigger callback
 *
 * @author Matt Ho
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RepositoryAspect {

  @NonNull
  final ApplicationEventPublisher publisher;
  @NonNull
  final Map<Class, List<AfterProceedCallback>> callbacks;

  @PreDestroy
  public void destroy() {
    SqlToUseHolder.clearContext();
  }

  @Pointcut("this(org.springframework.data.repository.Repository) || @annotation(org.springframework.stereotype.Repository) || @within(org.springframework.stereotype.Repository)")
  public void repository() {
  }

  @Pointcut("execution(* tw..*.*(..))")
  public void inPackageTw() {
    // 限制在 tw 下，避免抓到一堆亂七八糟的東西
  }

  @Around("repository() && inPackageTw()")
  public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
    var builder = builder(joinPoint);
    try {
      var proceed = joinPoint.proceed();
      builder
          .map(b -> b.sqlToUse(SqlToUseHolder.getContext()))
          .map(b -> b.proceed(proceed))
          .ifPresent(b -> triggerAllProxiedInterfaces(joinPoint, b));
      return proceed;
    } catch (Throwable t) {
      builder
          .map(b -> b.sqlToUse(SqlToUseHolder.getContext()))
          .map(b -> b.throwing(t))
          .ifPresent(b -> triggerAllProxiedInterfaces(joinPoint, b));
      throw t;
    } finally {
      SqlToUseHolder.clearContext();
    }
  }

  Optional<AfterProceedEvent.AfterProceedEventBuilder> builder(JoinPoint joinPoint) {
    try {
      var targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
      var invoked = MethodInvoked.builder()
          .declaringClass(targetMethod.getDeclaringClass())
          .name(targetMethod.toString())
          .simpleName(targetMethod.getName())
          .fullName(targetMethod.toGenericString())
          .build();
      return Optional.of(AfterProceedEvent.builder().invoked(invoked));
    } catch (Exception e) {
      log.error(
          "Failed to construct event builder for [{}]",
          ultimateTargetClass(joinPoint.getTarget()),
          e);
      return Optional.empty();
    }
  }

  /**
   * 判斷某的 class 是否來自於 spring-framework
   *
   * @param clazz
   * @return
   */
  boolean fromSpringframework(Class<?> clazz) {
    return clazz.getPackageName().startsWith("org.springframework");
  }

  void triggerAllProxiedInterfaces(
      @NonNull JoinPoint joinPoint, @NonNull AfterProceedEvent.AfterProceedEventBuilder builder) {
    StreamEx.of(proxiedUserInterfaces(joinPoint.getTarget()))
        // 我們大部分的 repository 幾乎都會繼承某些 spring 的 interface
        // 但實務上我們幾乎不會對 spring 的 interface 感興趣, 而是針對我們自己的 repository 去寫 listener 或
        // callback
        // 因此避免 publish 太多 event, 這邊我們固定 filter 掉 spring 相關的 interface 吧
        .filter(not(this::fromSpringframework))
        .map(proxiedInterface -> builder.repositoryClass(proxiedInterface))
        .map(AfterProceedEvent.AfterProceedEventBuilder::build)
        .forEach(this::triggerAfterProceed);
  }

  void triggerAfterProceed(@NonNull AfterProceedEvent event) {
    log.trace("Publishing event for {}: {}", event.getSource(), event);
    publisher.publishEvent(event);
    callbacks
        .getOrDefault(event.getSource(), emptyList())
        .forEach(
            callback -> {
              log.trace("Triggering callback for {}: {}", callback.getClass().getName(), event);
              callback.onAfterProceed(event);
            });
  }
}
