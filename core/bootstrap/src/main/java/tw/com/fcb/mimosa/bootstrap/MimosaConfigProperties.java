package tw.com.fcb.mimosa.bootstrap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;
import tw.com.fcb.mimosa.async.AsyncProperties;
import tw.com.fcb.mimosa.logging.LoggingProperties;
import tw.com.fcb.mimosa.tracing.TracingProperties;
import tw.com.fcb.mimosa.web.WebProperties;

// import tw.com.fcb.mimosa.logging.LoggingProperties;

/**
 * Rose properties
 *
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "mimosa")
public final class MimosaConfigProperties {

  /**
   * 日誌
   */
  @NestedConfigurationProperty
  private LoggingProperties logging = new LoggingProperties();

  /**
   * web
   */
  @NestedConfigurationProperty
  private WebProperties web = new WebProperties();

  /**
   * async
   */
  @NestedConfigurationProperty
  private AsyncProperties async = new AsyncProperties();

  /**
   * tracing
   */
  @NestedConfigurationProperty
  private TracingProperties tracing = new TracingProperties();

}
