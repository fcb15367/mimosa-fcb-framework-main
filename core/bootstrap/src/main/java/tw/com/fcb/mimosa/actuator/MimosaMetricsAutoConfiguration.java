package tw.com.fcb.mimosa.actuator;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.base.Strings;

import io.micrometer.core.instrument.MeterRegistry;
import tw.com.fcb.mimosa.bootstrap.BootstrapAutoConfiguration;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter(BootstrapAutoConfiguration.class)
public class MimosaMetricsAutoConfiguration {

  static final String SPRING_APPLICATION_NAME = "info.application.name";
  static final String SPRING_APPLICATION_VERSION = "info.application.version";

  @Bean
  @ConditionalOnProperty(SPRING_APPLICATION_NAME)
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
    return registry -> {
      registry.config().commonTags("application", lookup(environment, SPRING_APPLICATION_NAME));
      registry.config().commonTags("version", lookup(environment, SPRING_APPLICATION_VERSION));
    };
  }

  private String lookup(Environment environment, String key) {
    return Strings.isNullOrEmpty(environment.getProperty(key))
        ? "unknown"
        : environment.getProperty(key);
  }
}
