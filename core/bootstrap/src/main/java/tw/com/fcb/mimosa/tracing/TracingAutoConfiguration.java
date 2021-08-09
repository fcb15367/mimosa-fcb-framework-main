package tw.com.fcb.mimosa.tracing;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.opentracing.contrib.java.spring.jaeger.starter.TracerBuilderCustomizer;
import io.opentracing.contrib.spring.tracer.configuration.TracerAutoConfiguration;
import io.opentracing.contrib.spring.web.starter.WebTracingProperties;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.bootstrap.MimosaConfigProperties;
import tw.com.fcb.mimosa.tracing.ExtraPropagationFilter;
import tw.com.fcb.mimosa.tracing.MimosaScopeManager;
import tw.com.fcb.mimosa.tracing.ScopeInterceptor.ScopeInterceptorBuilder;
import tw.com.fcb.mimosa.tracing.TracedAspect;
import tw.com.fcb.mimosa.web.APIRequestFilter;
import tw.com.fcb.mimosa.web.APIResponseHandler;

/** @author Matt Ho */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(TracerAutoConfiguration.class)
@RequiredArgsConstructor
public class TracingAutoConfiguration {

  final MimosaConfigProperties properties;
  final ObjectMapper mapper;

  @Bean
  @ConditionalOnProperty(value = "mimosa.tracing.traced.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean(TracedAspect.class)
  TracedAspect tracedAspect(ObjectMapper mapper) {
    return new TracedAspect(properties.getTracing().getTraced(), mapper);
  }

  @Bean
  TracerBuilderCustomizer mimosaScopeManager(List<ScopeInterceptorBuilder> builders) {
    return builder -> builder.withScopeManager(new MimosaScopeManager(builders));
  }

  @Bean
  @ConditionalOnMissingBean(ExtraPropagationFilter.class)
  FilterRegistrationBean<ExtraPropagationFilter> extraPropagationFilter(
      WebTracingProperties tracingConfiguration, APIResponseHandler responseHandler) {
    var extraPropagationFilter = new ExtraPropagationFilter(
        properties.getTracing().getExtra().getPropagation(), responseHandler);
    var filterRegistrationBean = new FilterRegistrationBean<>(extraPropagationFilter);
    filterRegistrationBean.setUrlPatterns(tracingConfiguration.getUrlPatterns());
    filterRegistrationBean.setOrder(
        tracingConfiguration.getOrder() + 2); // 須確保順序是在 tracing filter 之後二個
    filterRegistrationBean.setAsyncSupported(true);
    return filterRegistrationBean;
  }

}
