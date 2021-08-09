package tw.com.fcb.mimosa.web;

import javax.annotation.PostConstruct;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.opentracing.Tracer;
import io.opentracing.contrib.spring.web.starter.WebTracingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.bootstrap.BootstrapAutoConfiguration;
import tw.com.fcb.mimosa.bootstrap.MimosaConfigProperties;
import tw.com.fcb.mimosa.domain.t9n.TranslationService;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(BootstrapAutoConfiguration.class)
public class WebApplicationConfiguration implements WebMvcConfigurer {

  private final MimosaConfigProperties properties;
  private final CorsEndpointProperties cors;
  private final Tracer tracer;
  private WebProperties web;

  @PostConstruct
  public void init() {
    this.web = properties.getWeb();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    var registration = cors.setMappings(registry);
    cors.setAllowedOrigins(registration);
    cors.setAllowedMethods(registration);
    cors.setAllowedHeaders(registration);
    cors.setExposedHeaders(registration);
    cors.setAllowCredentials(registration);
    cors.setMaxAge(registration);
  }

  @Bean
  @ConditionalOnMissingBean(BeanValidator.class)
  BeanValidator beanValidator(Validator validator, TranslationService translationService) {
    return new BeanValidator(validator, translationService);
  }

  @Bean
  @ConditionalOnMissingBean
  RestExceptionHandler restExceptionHandler(
      TranslationService translationService, APIResponseHandler responseHandler) {
    return new RestExceptionHandler(web.catchError, translationService, responseHandler) {
    };
  }

  @Bean
  @ConditionalOnProperty(value = "mimosa.web.enable-log", havingValue = "true", matchIfMissing = true)
  RequestLoggingHandler requestLoggingHandler() {
    return new RequestLoggingHandler(web);
  }

  @Bean
  TimeoutCallableProcessingInterceptor timeoutCallableProcessingInterceptor() {
    return new TimeoutCallableProcessingInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    if (web.enableLog) {
      registry.addInterceptor(requestLoggingHandler());
    }
    registry.addInterceptor(new B3PropagationInterceptor(tracer));
  }

  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(web.defaultAsyncTimeout);
    configurer.registerCallableInterceptors(timeoutCallableProcessingInterceptor());
  }

  @Bean
  APIResponseHandler apiResponseHandler(@Value("${info.response.id:unknown}") String responseId) {
    log.info(
        "Initializing APIResponseHandler for response-id: {}, strict mode: {}",
        responseId,
        web.strict);
    return new APIResponseHandler(responseId, web.strict);
  }

  @Bean
  APIResponseAspect apiResponseAspect(APIResponseHandler responseHandler, ObjectMapper mapper) {
    return new APIResponseAspect(responseHandler, mapper);
  }

  @Bean
  @ConditionalOnMissingBean(APIRequestFilter.class)
  FilterRegistrationBean<APIRequestFilter> apiRequestFilter(
      WebTracingProperties tracingConfiguration, ObjectMapper mapper) {
    var apiRequestFilter = new APIRequestFilter(mapper, web.requestBufferSize, web.requestBodyEncoding);
    var filterRegistrationBean = new FilterRegistrationBean<>(apiRequestFilter);
    filterRegistrationBean.setUrlPatterns(tracingConfiguration.getUrlPatterns());
    filterRegistrationBean.setOrder(
        tracingConfiguration.getOrder() + 1); // 須確保順序是在 tracing filter 之後一個
    filterRegistrationBean.setAsyncSupported(true);
    return filterRegistrationBean;
  }
}
