package tw.com.fcb.mimosa.bootstrap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import io.undertow.UndertowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.web.CorsEndpointProperties;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = { "tw.com.fcb.mimosa" })
@EnableConfigurationProperties({ MimosaConfigProperties.class, CorsEndpointProperties.class })
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true)
@EnableRetry(proxyTargetClass = true)
@Slf4j
@RequiredArgsConstructor
public class BootstrapAutoConfiguration {

  final MimosaConfigProperties properties;

  @Bean
  @ConditionalOnWebApplication
  UndertowBuilderCustomizer undertowBuilderCustomizer() {
    return builder -> builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true);
  }
}
