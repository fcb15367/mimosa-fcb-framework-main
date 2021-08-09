package tw.com.fcb.mimosa.ext.security.auth;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import lombok.NonNull;
import lombok.Synchronized;
import tw.com.fcb.mimosa.ext.security.UserDetailsByUsernameService;

/** @author Matt Ho */
public class RestAuthClient implements AuthClient, BeanFactoryAware {

  private final String url;

  private BeanFactory factory;
  private volatile RestTemplate restTemplate;

  RestAuthClient(@NonNull String baseUrl) {
    this.url = fromHttpUrl(baseUrl).path("/validateToken").build().toUriString();
  }

  @Override
  public ValidateResponse validateToken(@NonNull ValidateRequest request) {
    return restTemplate().postForObject(url, request, ValidateResponse.class);
  }

  /**
   * 我們 follow Spring 官方建議:
   *
   * <ol>
   * <li>透過 {@link RestTemplateBuilder} 來建置 {@link RestTemplate}
   * <li>在每個 spring bean 中 cache 一個 {@link RestTemplate} instance
   * </ol>
   *
   * 但我們卻不依照官方的範例在 Bean initial 階段 (如在 constructor 中) 就把 {@link RestTemplate} 建立起來, 是因為這樣會造成 app
   * 在啟動時會發生例外:
   *
   * <pre>
   * {@code Caused by:
   * org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with
   * name 'tracer': Requested bean is currently in creation: Is there an unresolvable circular
   * reference }
   * </pre>
   *
   * 估計是因為以下原因, 造成在 initial 上的順序, 依賴等複雜問題所產生 circular reference:
   *
   * <ol>
   * <li>本 class ({@code RestAuthClient}) 會在 {@link UserDetailsByUsernameService} 被使用到, which 是在
   * {@link WebApplicationContext} 中
   * <li>{@link RestTemplateBuilder} 是會收集很多 {@link RestTemplateCustomizer} 實作, 很多是在 OpenTracing 的
   * auto configure 被 initial, which 是在 {@link ApplicationContext} 中
   * </ol>
   *
   * 因此我們透過此 method 做到 lazy initial, 在此 class 中只需要拿到 {@link BeanFactory}, 實際在使用時才建構出來
   *
   * @see <a
   *      href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.resttemplate">
   *      Calling REST Services with RestTemplate</a>
   */
  @Synchronized
  private RestTemplate restTemplate() {
    if (this.restTemplate == null) {
      this.restTemplate = factory
          .getBean(RestTemplateBuilder.class)
          .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .build();
    }
    return restTemplate;
  }

  @Override
  public void setBeanFactory(BeanFactory factory) throws BeansException {
    this.factory = factory;
  }
}
