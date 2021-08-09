package tw.com.fcb.mimosa.ext.openfeign.autoconfigure;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import feign.Client;
import feign.Logger;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.openfeign.SpringSecurityRequestInterceptor;

@RequiredArgsConstructor
@Configuration
@Import(FeignClientsConfiguration.class)
@EnableConfigurationProperties(MimosaOpenFeignProperties.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
@Slf4j
public class MimosaOpenFeignAutoConfiguration {

  final MimosaOpenFeignProperties properties;

  /**
   * @see <a
   *      href="https://github.com/opentracing-contrib/java-spring-jaeger#trace-id-not-propagated-via-the-feign-client">Trace
   *      id not propagated via the Feign client</a>
   * @return
   */
  @Bean
  @ConditionalOnMissingBean
  public Client feignClient() {
    return new Client.Default(null, null);
  }

  /**
   * @see <a
   *      href="https://cloud.spring.io/spring-cloud-openfeign/2.2.x/reference/html/#spring-matrixvariable-support">Feign
   *      logging</a>
   */
  @Bean
  @ConditionalOnMissingBean
  public Logger.Level feignLoggerLevel() {
    return properties.getLogger().getLevel();
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty("mimosa.openfeign.interceptor.security.enabled")
  public SpringSecurityRequestInterceptor springSecurityRequestInterceptor() {
    return new SpringSecurityRequestInterceptor(properties.getInterceptor().getSecurity());
  }
}
