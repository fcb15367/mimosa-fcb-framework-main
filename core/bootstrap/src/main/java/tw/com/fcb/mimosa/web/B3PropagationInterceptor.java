package tw.com.fcb.mimosa.web;

import static java.lang.Long.toHexString;
import static java.lang.String.valueOf;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 將 B3 Propagation 的 header 放入 response 中為了方便 API Consumer 查看
 *
 * @author Matt Ho
 */
@RequiredArgsConstructor
public class B3PropagationInterceptor implements HandlerInterceptor {

  protected static final String TRACE_ID_NAME = "X-B3-TraceId";
  protected static final String SPAN_ID_NAME = "X-B3-SpanId";
  protected static final String PARENT_SPAN_ID_NAME = "X-B3-ParentSpanId";
  protected static final String SAMPLED_NAME = "X-B3-Sampled";

  private final Tracer tracer;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    Optional.ofNullable(tracer)
        .flatMap(t -> Optional.ofNullable(t.scopeManager()))
        .flatMap(sm -> Optional.ofNullable(sm.activeSpan()))
        .flatMap(span -> Optional.ofNullable(span.context()))
        .ifPresent(context -> inject(response, context));
    return true;
  }

  /**
   * inject header from {@code context} to {@code response}
   *
   * @param response inject to
   * @param context inject from
   */
  protected void inject(@NonNull HttpServletResponse response, @NonNull SpanContext context) {
    response.setHeader(TRACE_ID_NAME, context.toTraceId());
    if (context instanceof JaegerSpanContext) {
      var jsc = (JaegerSpanContext) context;
      response.setHeader(SPAN_ID_NAME, toHexString(jsc.getSpanId()));
      response.setHeader(SAMPLED_NAME, valueOf(jsc.isSampled()));
      response.setHeader(PARENT_SPAN_ID_NAME, toHexString(jsc.getParentId()));
    }
  }
}
