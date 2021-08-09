package tw.com.fcb.mimosa.tracing;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.contrib.java.spring.jaeger.starter.TracerBuilderCustomizer;

/**
 * 由於在 {@link TracerBuilderCustomizer} 中 {@link io.opentracing.ScopeManager} 只能客製一個, 但我們有需求會有多種在
 * Scope 建構之後跟結束之前要做的邏輯, 因此開出了這個 interface 做 {@link Scope} 的 intercepting, 統一由 {@link
 * MimosaScopeManager} 來呼叫使用
 *
 * <p>
 * 只要專案使用時在 spring 中放入 {@link ScopeInterceptorBuilder} bean, 就會自動的被掃描到
 *
 * @author Matt Ho
 */
public interface ScopeInterceptor {

  void preHandle(Scope scope, Span activeSpan);

  void postHandle(Scope scope, Span activeSpan);

  /** spring 掃描的目標 class, 目的是做到 prototype */
  interface ScopeInterceptorBuilder {

    ScopeInterceptor build();
  }
}
