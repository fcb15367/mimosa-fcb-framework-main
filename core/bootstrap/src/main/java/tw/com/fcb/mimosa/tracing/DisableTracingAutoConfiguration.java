package tw.com.fcb.mimosa.tracing;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.opentracing.Tracer;
import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;
import io.opentracing.noop.NoopTracerFactory;

/**
 * 關閉 opentracing 的相關配置, 理應 run app 不會這樣配置 (或許在 test 情境中有機會關閉)
 *
 * @author Matt Ho
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(TracerAutoConfiguration.class)
@ConditionalOnProperty(value = "opentracing.jaeger.enabled", havingValue = "false")
public class DisableTracingAutoConfiguration {

  /**
   * 這樣才能完全關閉 Jaeger, 也才可以避免掉一個很吵的 log: Service name must not be null or empty...
   *
   * @see <a
   *      href="https://github.com/opentracing-contrib/java-spring-jaeger/issues/73>https://github.com/opentracing-contrib/java-spring-jaeger/issues/73</a>
   * @see <a href=
   *      "https://github.com/opentracing-contrib/java-spring-jaeger#completely-disable-tracing">https://github.com/opentracing-contrib/java-spring-jaeger#completely-disable-tracing</a>
   */
  @Bean
  public Tracer jaegerTracer() {
    return NoopTracerFactory.create();
  }
}
