package tw.com.fcb.mimosa.tracing;

import static java.util.Optional.empty;
import static lombok.AccessLevel.PRIVATE;

import java.util.Optional;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@NoArgsConstructor(access = PRIVATE)
public class TraceContext {

  /**
   * 取得當前 tracer
   *
   * @return
   */
  public static Optional<Tracer> getTracer() {
    if (GlobalTracer.isRegistered()) {
      return Optional.of(GlobalTracer.get());
    }
    return empty();
  }

  /**
   * 取得當前 active span
   *
   * @return
   */
  public static Optional<Span> getActiveSpan() {
    return getTracer().map(Tracer::activeSpan);
  }

  /**
   * 取得當前 Jaeger span context
   *
   * @return
   */
  public static Optional<JaegerSpanContext> getJaegerSpanContext() {
    return getActiveSpan()
        .map(Span::context)
        .filter(JaegerSpanContext.class::isInstance)
        .map(JaegerSpanContext.class::cast);
  }

  /**
   * 取得當前 trace ID
   *
   * @return
   */
  public static Optional<String> getTraceId() {
    return getJaegerSpanContext().map(JaegerSpanContext::getTraceId);
  }

  /**
   * 取得當前 trace 中的夾帶的變數
   *
   * @return
   */
  public static Optional<String> getBaggageItem(@NonNull String key) {
    return getJaegerSpanContext().map(ctx -> ctx.getBaggageItem(key));
  }

  /** 設定變數到 trace 中, key 或 value 任一為 null 就不會有實際作為 */
  public static void setBaggageItem(String key, String value) {
    getActiveSpan().ifPresent(span -> span.setBaggageItem(key, value));
  }
}
