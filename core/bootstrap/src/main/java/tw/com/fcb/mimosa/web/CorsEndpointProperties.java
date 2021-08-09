package tw.com.fcb.mimosa.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.google.common.collect.Lists;

import lombok.Data;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "spring.web.cors")
public class CorsEndpointProperties {

  private List<String> mappings = Lists.newArrayList();

  /**
   * Comma-separated list of origins to allow. '*' allows all origins. When not set, CORS support is
   * disabled.
   */
  private List<String> allowedOrigins = Lists.newArrayList();

  /**
   * Comma-separated list of methods to allow. '*' allows all methods. When not set, defaults to
   * GET.
   */
  private List<String> allowedMethods = Lists.newArrayList();

  /** Comma-separated list of headers to allow in a request. '*' allows all headers. */
  private List<String> allowedHeaders = Lists.newArrayList();

  /** Comma-separated list of headers to include in a response. */
  private List<String> exposedHeaders = Lists.newArrayList();

  /** Whether credentials are supported. When not set, credentials are not supported. */
  private boolean allowCredentials = false;

  /**
   * How long the response from a pre-flight request can be cached by clients. If a duration suffix
   * is not specified, seconds will be used.
   */
  @DurationUnit(ChronoUnit.SECONDS)
  private Duration maxAge = Duration.ofSeconds(1800);

  CorsRegistration setMappings(CorsRegistry registry) {
    if (getMappings().isEmpty()) {
      return registry.addMapping("/**");
    } else {
      return registry.addMapping(String.join(",", getMappings()));
    }
  }

  CorsRegistration setAllowedOrigins(CorsRegistration registration) {
    if (getAllowedOrigins().isEmpty()) {
      return registration.allowedOrigins("*");
    } else {
      return registration.allowedOrigins(getAllowedOrigins().toArray(new String[] {}));
    }
  }

  CorsRegistration setAllowedMethods(CorsRegistration registration) {
    if (getAllowedMethods().isEmpty()) {
      return registration.allowedMethods("*");
    } else {
      return registration.allowedMethods(getAllowedMethods().toArray(new String[] {}));
    }
  }

  CorsRegistration setAllowedHeaders(CorsRegistration registration) {
    if (getAllowedHeaders().isEmpty()) {
      return registration.allowedHeaders("*");
    } else {
      return registration.allowedHeaders(getAllowedHeaders().toArray(new String[] {}));
    }
  }

  CorsRegistration setExposedHeaders(CorsRegistration registration) {
    getExposedHeaders().add(B3PropagationInterceptor.TRACE_ID_NAME);
    getExposedHeaders().add(B3PropagationInterceptor.SPAN_ID_NAME);
    getExposedHeaders().add(B3PropagationInterceptor.SAMPLED_NAME);
    getExposedHeaders().add(B3PropagationInterceptor.PARENT_SPAN_ID_NAME);
    return registration.exposedHeaders(getExposedHeaders().toArray(new String[] {}));
  }

  CorsRegistration setAllowCredentials(CorsRegistration registration) {
    return registration.allowCredentials(isAllowCredentials());
  }

  CorsRegistration setMaxAge(CorsRegistration registration) {
    return registration.maxAge(getMaxAge().getSeconds());
  }
}
