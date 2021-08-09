package tw.com.fcb.mimosa.tracing;

import java.util.Map;

import org.springframework.util.StopWatch;

import io.opentracing.Scope;
import io.opentracing.Span;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

/**
 * 已經被監控的時間, 同時也實作了 auto closable
 *
 * @see StopWatch
 * @author Matt Ho
 */
@Getter(AccessLevel.PACKAGE)
public class TracedCloseable implements AutoCloseable {

  TracedCloseable(@NonNull String name, boolean buildSpan, @NonNull Map<String, String> tags) {
    this.watch = new StopWatch(name);
    this.watch.start(name);
    if (buildSpan) {
      var registeredTracer = TraceContext.getTracer();
      if (registeredTracer.isPresent()) {
        var tracer = registeredTracer.get();
        var spanBuilder = tracer.buildSpan(name);
        tags.forEach(spanBuilder::withTag);
        this.span = spanBuilder.start();
        this.scope = tracer.activateSpan(this.span);
      }
    }
  }

  final StopWatch watch;
  Span span;
  Scope scope;

  @Override
  public void close() {
    watch.stop();
    if (scope != null) {
      scope.close();
    }
    if (span != null) {
      span.finish();
    }
  }
}
