package tw.com.fcb.mimosa.ext.cache;

import static lombok.AccessLevel.PRIVATE;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.Weigher;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@ConfigurationProperties(prefix = "mimosa.cache")
public class MimosaCacheProperties {

  @Singular
  private Set<String> cacheNames = Sets.newHashSet();

  /** 是否防止 cache 穿透 */
  @Default
  private boolean cacheNullValues = true;

  /** 是否根據 cacheName 動態建立 cache */
  @Default
  private boolean dynamic = true;

  @Default
  private String cachePrefix = "";

  @Default
  private RedisProperties redis = new RedisProperties();

  @Default
  private CaffeineProperties caffeine = new CaffeineProperties();

  @SuperBuilder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor(access = PRIVATE)
  public static class RedisProperties extends Redis {
    @Default
    Map<String, Redis> names = new HashMap<>();

    public Redis find(@NonNull String name) {
      return names.entrySet().stream()
          .filter(e -> e.getKey().equals(name))
          .map(Entry::getValue)
          .findFirst()
          .orElse(this);
    }

    public Collection<ChannelTopic> getChannelTopics() {
      return Stream.concat(
          Stream.of(determineTopic(null)),
          getNames().entrySet().stream().map(e -> e.getValue().determineTopic(e.getKey())))
          .map(ChannelTopic::new)
          .collect(Collectors.toList());
    }
  }

  @SuperBuilder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor(access = PRIVATE)
  public static class Redis {

    /** cache 過期時間，單位 ms，預設不過期 */
    @Default
    private long defaultExpiration = 0;

    /** 每個 cacheName 的過期時間，單位 ms */
    @Singular
    private Map<String, Long> expires = Maps.newHashMap();

    /** 更新時通知其他節點的 topic 名稱 */
    private String topic;

    public String determineTopic(String name) {
      if (!StringUtils.isEmpty(topic)) {
        return topic;
      }
      return Stream.of("mimosa:cache:redis:caffeine:topic", name)
          .filter(Objects::nonNull)
          .collect(Collectors.joining(":"));
    }
  }

  @SuperBuilder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor(access = PRIVATE)
  public static class CaffeineProperties extends Caffeine {
    @Default
    Map<String, Caffeine> names = new HashMap<>();

    public Caffeine find(@NonNull String name) {
      return names.entrySet().stream()
          .filter(e -> e.getKey().equals(name))
          .map(Entry::getValue)
          .findFirst()
          .orElse(this);
    }
  }

  @SuperBuilder
  @Data
  @NoArgsConstructor
  @AllArgsConstructor(access = PRIVATE)
  public static class Caffeine {

    /** 存取後的過期時間，單位 ms */
    private long expireAfterAccess;

    /** 寫入後的過期時間，單位 ms */
    private long expireAfterWrite;

    /** 更新後的過期時間，單位 ms */
    private long refreshAfterWrite;

    /** 初始化大小 */
    private int initialCapacity;

    /** 最大 cache 數量 */
    private long maximumSize;

    /** 最大 cache weight */
    private long maximumWeight;

    /** Weigher class, 必須實作 {@link Weigher} */
    private Class<Weigher> weigher;

    /**
     * 設定 week keys
     *
     * <p>
     * Caffeine.weakKeys() stores keys using weak references. This allows entries to be
     * garbage-collected if there are no other strong references to the keys. Since garbage
     * collection depends only on identity equality, this causes the whole cache to use identity
     * (==) equality to compare keys, instead of equals().
     *
     * @see <a
     *      href=
     *      "https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based">https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based</a>
     */
    private Boolean weakKeys;

    /**
     * 設定 week values
     *
     * <p>
     * Caffeine.weakValues() stores values using weak references. This allows entries to be
     * garbage-collected if there are no other strong references to the values. Since garbage
     * collection depends only on identity equality, this causes the whole cache to use identity
     * (==) equality to compare values, instead of equals().
     *
     * @see <a
     *      href=
     *      "https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based">https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based</a>
     */
    private Boolean weakValues;

    /**
     * 設定 soft values
     *
     * <p>
     * Caffeine.softValues() stores values using soft references. Softly referenced objects are
     * garbage-collected in a globally least-recently-used manner, in response to memory demand.
     * Because of the performance implications of using soft references, we generally recommend
     * using the more predictable maximum cache size instead. Use of softValues() will cause values
     * to be compared using identity (==) equality instead of equals().
     *
     * @see <a
     *      href=
     *      "https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based">https://github.com/ben-manes/caffeine/wiki/Eviction#reference-based</a>
     */
    private Boolean softValues;
  }
}
