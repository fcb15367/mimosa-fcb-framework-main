package tw.com.fcb.mimosa.ext.openfeign;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

import java.util.Base64;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.openfeign.autoconfigure.MimosaOpenFeignProperties;

/**
 * 這隻程式提供了沒有啟用 mimosa-security-ext 的 App 在使用 Feign 時, 自動的將當前 Spring Security 的 Principal 傳遞出去, 設定
 * "mimosa.openfeign.interceptor.security.enabled=true" 可啟動 interceptor (預設 false)
 *
 * <p>
 * 假設 A 服務要透過 Feign 呼叫 B 服務, 以下幾種狀況:
 *
 * <p>
 * 1. A 跟 B 都有開啟 mimosa-security-ext, 則 A 不用啟動這隻 interceptor, Principal 會透過 jaeger baggage item
 * 傳遞
 *
 * <p>
 * 2. A 沒開啟 mimosa-security-ext, B 有開啟, 則 A 需要啟動這隻 interceptor 來傳遞 Principal
 *
 * <p>
 * 3. A 有開啟 mimosa-security-ext, B 沒有, 則 A 不用啟動這隻 interceptor, 不需傳遞任何的 Principal
 *
 * @author Matt Ho
 */
@Slf4j
@RequiredArgsConstructor
public class SpringSecurityRequestInterceptor implements RequestInterceptor {

  @NonNull
  final MimosaOpenFeignProperties.Security security;

  @Override
  public void apply(RequestTemplate template) {
    if (template.headers().containsKey(security.getHeader().getPrincipal())
        && !security.getHeader().isOverride()) {
      log.debug(
          "Already contains header [{}] for {}, skip overriding it from Spring Security",
          security.getHeader().getPrincipal(),
          template.headers().get(security.getHeader().getPrincipal()));
      return;
    }
    ofNullable(SecurityContextHolder.getContext().getAuthentication())
        .map(Authentication::getPrincipal)
        .filter(UserDetails.class::isInstance)
        .map(UserDetails.class::cast)
        .map(UserDetails::getUsername)
        .flatMap(this::authToken)
        .ifPresent(token -> template.header(security.getHeader().getPrincipal(), token));
  }

  Optional<String> authToken(@NonNull String username) {
    log.debug(
        "Encoding username [{}] to {}-token", username, security.getTokenType().toLowerCase());
    // Mimosa framework 的 security-ext 模組是不需要密碼的, 為求符合標準 basic auth token, 故這邊密碼就固定給空字串
    var token = format("%s:%s", username, "");
    try {
      var encoded = Base64.getEncoder().encodeToString(token.getBytes(security.getCharset()));
      return Optional.of(format("%s %s", security.getTokenType(), encoded));
    } catch (Exception e) {
      log.error("Error base64 encoding for: {}", username, e);
      return Optional.empty();
    }
  }
}
