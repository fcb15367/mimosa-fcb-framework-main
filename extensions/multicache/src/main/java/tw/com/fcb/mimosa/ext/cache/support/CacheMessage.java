package tw.com.fcb.mimosa.ext.cache.support;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class CacheMessage implements Serializable {
  private String cacheName;
  private Object key;
}
