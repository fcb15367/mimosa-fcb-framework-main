package tw.com.fcb.mimosa.ext.openfeign.autoconfigure;

import static feign.Logger.Level.BASIC;
import static feign.Util.ISO_8859_1;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "mimosa.openfeign")
public class MimosaOpenFeignProperties {

  Logger logger = new Logger();
  Interceptor interceptor = new Interceptor();

  @Data
  public static class Logger {
    feign.Logger.Level level = BASIC;
  }

  @Data
  public static class Interceptor {
    Security security = new Security();
  }

  @Data
  public static class Security {
    boolean enabled = false;
    Header header = new Header();
    Charset charset = ISO_8859_1;
    String tokenType = "Basic";
  }

  @Data
  public static class Header {
    /** 當 header 已經有對應的 key 了, 是否還要 override */
    boolean override = false;

    String principal = "Authorization";
    String credentials = "Authorization";
  }
}
