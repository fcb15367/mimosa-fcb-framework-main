package tw.com.fcb.mimosa.logging;

import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.MDC;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import tw.com.fcb.mimosa.tracing.ScopeInterceptor;
import tw.com.fcb.mimosa.tracing.TracingProperties.Propagation;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
public class MDCScopeInterceptor implements ScopeInterceptor {

  final List<String> extras;

  private String previousTraceId;
  private String previousSpanId;
  private String previousParentSpanId;
  private String previousSampled;
  private Map<String, Optional<String>> previousExtras;

  public MDCScopeInterceptor(@NonNull List<Propagation> extras) {
    this.extras = extras.stream().map(Propagation::getBaggage).collect(toList());
  }

  @Override
  public void preHandle(Scope scope, Span activeSpan) {
    this.previousTraceId = lookup("traceId");
    this.previousSpanId = lookup("spanId");
    this.previousParentSpanId = lookup("parentSpanId");
    this.previousSampled = lookup("traceSampled");
    this.previousExtras = unmodifiableMap(extras.stream().collect(toMap(identity(), key -> ofNullable(lookup(key)))));

    JaegerSpanContext ctx = (JaegerSpanContext) activeSpan.context();
    String traceId = ctx.getTraceId();
    String spanId = Long.toHexString(ctx.getSpanId());
    String sampled = String.valueOf(ctx.isSampled());
    String parentSpanId = Long.toHexString(ctx.getParentId());

    replace("traceId", traceId);
    replace("spanId", spanId);
    replace("parentSpanId", parentSpanId);
    replace("traceSampled", sampled);
    StreamEx.of(extras)
        .mapToEntry(identity(), ctx::getBaggageItem)
        .forKeyValue(MDCScopeInterceptor::replace);
  }

  @Override
  public void postHandle(Scope scope, Span activeSpan) {
    replace("traceId", previousTraceId);
    replace("spanId", previousSpanId);
    replace("parentSpanId", previousParentSpanId);
    replace("traceSampled", previousSampled);
    EntryStream.of(previousExtras.entrySet().stream())
        .mapValues(value -> value.orElse(null))
        .forKeyValue(MDCScopeInterceptor::replace);
  }

  private static String lookup(String key) {
    return MDC.get(key);
  }

  private static void replace(String key, String value) {
    if (Objects.isNull(value)) {
      MDC.remove(key);
    } else {
      MDC.put(key, value);
    }
  }
}
