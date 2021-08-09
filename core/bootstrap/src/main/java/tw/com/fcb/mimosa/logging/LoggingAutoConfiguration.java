package tw.com.fcb.mimosa.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.bootstrap.MimosaConfigProperties;
import tw.com.fcb.mimosa.tracing.ScopeInterceptor.ScopeInterceptorBuilder;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class LoggingAutoConfiguration {

  final MimosaConfigProperties properties;

  @Bean
  ScopeInterceptorBuilder mdcScopeLifecycle() {
    return () -> new MDCScopeInterceptor(properties.getTracing().getExtra().getPropagation());
  }
}
