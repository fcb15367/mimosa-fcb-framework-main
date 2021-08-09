package tw.com.fcb.mimosa.tracing;

import static java.util.stream.Collectors.toList;

import java.util.Collection;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.util.ThreadLocalScopeManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.tracing.ScopeInterceptor.ScopeInterceptorBuilder;

/** @author Matt Ho */
@RequiredArgsConstructor
class MimosaScopeManager extends ThreadLocalScopeManager {

  @NonNull
  final Collection<ScopeInterceptorBuilder> builders;

  @Override
  public Scope activate(Span span) {
    var scope = super.activate(span);
    return new ScopeWrapper(scope, span, builders);
  }

  private static class ScopeWrapper implements Scope {

    final Scope scope;
    final Span span;
    final Collection<ScopeInterceptor> interceptors;

    public ScopeWrapper(
        @NonNull Scope scope,
        @NonNull Span span,
        @NonNull Collection<ScopeInterceptorBuilder> builders) {
      this.scope = scope;
      this.span = span;
      this.interceptors = builders.stream().map(ScopeInterceptorBuilder::build).collect(toList());
      this.interceptors.forEach(interceptor -> interceptor.preHandle(scope, span));
    }

    @Override
    public void close() {
      this.scope.close();
      this.interceptors.forEach(interceptor -> interceptor.postHandle(scope, span));
    }
  }
}
