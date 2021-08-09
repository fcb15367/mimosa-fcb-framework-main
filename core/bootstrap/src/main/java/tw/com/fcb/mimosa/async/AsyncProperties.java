package tw.com.fcb.mimosa.async;

import lombok.Data;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Data
public class AsyncProperties {

  int corePoolSize = 1;

  int maxPoolSize = Integer.MAX_VALUE;

  int keepAliveSeconds = 60;

  int queueCapacity = Integer.MAX_VALUE;

  boolean allowCoreThreadTimeout = false;
}
