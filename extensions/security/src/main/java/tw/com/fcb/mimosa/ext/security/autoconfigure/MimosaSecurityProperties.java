package tw.com.fcb.mimosa.ext.security.autoconfigure;

import static java.net.URLDecoder.decode;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jaegertracing.internal.JaegerSpanContext;
import io.opentracing.Span;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@ConfigurationProperties(prefix = "mimosa.security")
public class MimosaSecurityProperties {

  /** 啟動 mimosa security * */
  boolean enabled = true;

  String realm = "mimosa";
  /** 嚴格模式, 若為 false 有傳 token 才驗證, 沒傳使用 anonymous 使用者登入, 反之 token 一定要傳且通過驗證 */
  boolean strict = true;

  Collection<Ignoring> ignoring = new ArrayList<>();
  Header header = new Header();

  /** 客戶端後端認証服務 */
  BackendAuthService backendAuthService = new BackendAuthService();

  @Data
  public static class BackendAuthService {
    String url;
  }

  @Data
  public static class Ignoring {
    HttpMethod method;
    Collection<String> path = new ArrayList<>();
  }

  @Data
  public static class Header {
    String principal = "Authorization";
    String credentials;

    public Baggage toBaggage(@NonNull ObjectMapper mapper) {
      return new Baggage(this, mapper);
    }
  }

  /**
   * 由於 jaeger 針對 Baggage key 的大小寫有處理過, 為了避免程式混淆搞, 這邊不管 user 設定 header.principal 或
   * header.credentials 是大寫還是小寫, 程式統一都在 Baggage 用小寫‍
   */
  public static class Baggage {

    @Getter
    final String principalKey;
    @Getter
    final String credentialsKey;
    final ObjectMapper mapper;

    Baggage(@NonNull Header header, @NonNull ObjectMapper mapper) {
      this.principalKey = header.principal.toLowerCase();
      this.credentialsKey = ofNullable(header.credentials).map(String::toLowerCase).orElse("credentials");
      this.mapper = mapper;
    }

    @SneakyThrows
    public void setPrincipalToSpan(Span span, Object principal) {
      if (span != null && principal != null) {
        span.setBaggageItem(this.principalKey, encode(mapper.writeValueAsString(principal), UTF_8));
      }
    }

    public void setCredentialsToSpan(Span span, Object credentials) {
      if (span != null && credentials != null) {
        span.setBaggageItem(this.credentialsKey, encode(credentials.toString(), UTF_8));
      }
    }

    public Optional<String> getPrincipalFromSpan(Span span) {
      return ofNullable(span)
          .map(Span::context)
          .filter(JaegerSpanContext.class::isInstance)
          .map(JaegerSpanContext.class::cast)
          .map(context -> context.getBaggageItem(principalKey))
          .map(baggage -> decode(baggage, UTF_8));
    }

    public Optional<String> getCredentialsFromSpan(Span span) {
      return ofNullable(span)
          .map(Span::context)
          .filter(JaegerSpanContext.class::isInstance)
          .map(JaegerSpanContext.class::cast)
          .map(context -> context.getBaggageItem(credentialsKey))
          .map(baggage -> decode(baggage, UTF_8));
    }
  }
}
