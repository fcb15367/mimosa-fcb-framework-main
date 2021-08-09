package tw.com.fcb.mimosa.ext.cache.support;

import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableSet;
import static java.util.Optional.ofNullable;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;

import io.micrometer.core.instrument.Tag;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.cache.MimosaCacheProperties;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
@Builder
public class MimosaCacheManager implements CacheManager {

  public static final String CACHE_MANAGER_NAME = "mimosa";
  private static final Tag cacheManagerTag = Tag.of("cacheManager", CACHE_MANAGER_NAME);
  private final MimosaCacheProperties cacheProperties;
  private final RedisTemplate<String, Object> stringKeyRedisTemplate;
  private final ObjectProvider<CacheMetricsRegistrar> cacheMetricsRegistrarProvider;

  @Default
  private Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

  @Default
  private boolean dynamic = true;

  @Override
  public Cache getCache(String name) {
    var cache = cacheMap.get(name);
    if (Objects.nonNull(cache)) {
      return cache;
    }
    if (!dynamic && !getCacheNames().contains(name)) {
      return cache;
    }
    return cacheMap.computeIfAbsent(
        name,
        cacheName -> {
          var caffeineSettings = cacheProperties.getCaffeine().find(cacheName);
          var redisSettings = cacheProperties.getRedis().find(cacheName);
          log.debug(
              "creating [{}] cache with caffeine settings: {}, redis settings: {}",
              cacheName,
              caffeineSettings,
              redisSettings);
          var caffeineCache = caffeineCache(caffeineSettings);
          bindCacheToRegistry(cacheName, caffeineCache);
          return new MimosaCache(
              cacheName, stringKeyRedisTemplate, caffeineCache, cacheProperties, redisSettings);
        });
  }

  /**
   * 綁定 Cache 到 CacheMetricsRegistrar 中
   *
   * <p>
   * 動態建立的 Cache 需要自己註冊到 metricsRegistrar 中, 因此我們跟著 {@link
   * org.springframework.boot.actuate.autoconfigure.metrics.cache.CacheMetricsRegistrarConfiguration#bindCacheToRegistry(String,
   * Cache)} 使用同樣的邏輯去註冊
   *
   * @param cacheName
   * @param cache
   * @see <a href=
   *      "https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html#production-ready-metrics-cache">Cache
   *      Metrics</a>
   */
  private void bindCacheToRegistry(
      @NonNull String cacheName,
      @NonNull com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
    ofNullable(cacheMetricsRegistrarProvider.getIfAvailable())
        .ifPresent(
            registrar -> {
              log.debug(
                  "binding cache [{}] to registry: {}", cacheName, registrar.getClass().getName());
              registrar.bindCacheToRegistry(new CaffeineCache(cacheName, cache), cacheManagerTag);
            });
  }

  public Optional<Object> get(String cacheName, Object key) {
    return ofNullable(getCache(cacheName).get(key)).map(ValueWrapper::get);
  }

  public <T> T get(String cacheName, Object key, @Nullable Class<T> type) {
    return getCache(cacheName).get(key, type);
  }

  public <T> T get(String cacheName, Object key, @NonNull TypeReference<T> type) {
    return getCache(cacheName).get(key, getRawType(type));
  }

  /**
   * 取得 {@code cacheName} 中的 {@code key} 資料, 若沒快取過則呼叫 {@code loader} 取得資料並快取之
   *
   * <p>
   * 此方法方便用於 dynamic cache key, 程式碼範例: <code>
   * <pre>
   *  static final String CACHE_NAME = "my:cache:name";
   *
   *  final RoseCacheManager cacheManager;
   *
   *  Optional&lt;Graph&gt; getGraph(Key key) {
   *    return cacheManager.get(
   *        CACHE_NAME,
   *        key,
   *        Graph.class,
   *        this::createExpensiveGraph);
   *  }
   *
   *  Optional&lt;Graph&gt; createExpensiveGraph(Key key) {
   *    ...
   *  }
   * </pre>
   * </code>
   *
   * @see #get(String, Object, Class, DataSupplier)
   */
  public <K, T> Optional<T> get(
      String cacheName, K key, @Nullable Class<T> type, @NonNull Function<K, Optional<T>> loader) {
    return get(cacheName, key, type, () -> loader.apply(key));
  }

  public <K, T> Optional<T> get(
      String cacheName,
      K key,
      @NonNull TypeReference<T> type,
      @NonNull Function<K, Optional<T>> loader) {
    return get(cacheName, key, getRawType(type), loader);
  }

  /**
   * 取得 {@code cacheName} 中的 {@code key} 資料, 若沒快取過則呼叫 {@code supplier} 取得資料並快取之
   *
   * <p>
   * 此方法方便用於 static cache key, 程式碼範例: <code>
   * <pre>
   *  static final String CACHE_NAME = "my:cache:name";
   *  static final String CACHE_KEY = "mykey";
   *
   *  final RoseCacheManager cacheManager;
   *
   *  Optional&lt;Graph&gt; getGraph() {
   *    return cacheManager.get(
   *        CACHE_NAME,
   *        CACHE_KEY,
   *        Graph.class,
   *        this::createExpensiveGraph);
   *  }
   *
   *  Optional&lt;Graph&gt; createExpensiveGraph() {
   *    ...
   *  }
   * </pre>
   * </code>
   *
   * @see #get(String, Object, Class, Function)
   */
  public <T> Optional<T> get(
      String cacheName, Object key, @Nullable Class<T> type, DataSupplier<Optional<T>> supplier) {
    T cached = get(cacheName, key, type);
    if (Objects.nonNull(cached)) {
      return Optional.of(cached);
    } else if (Objects.nonNull(supplier)) {
      log.debug("cannot find cached data with key [{}:{}]", cacheName, key);
      log.debug("now create data for key [{}:{}]", cacheName, key);
      var optional = supplier.create();
      if (optional.isPresent()) {
        T result = optional.get();
        put(cacheName, key, result);
        log.debug("key [{}:{}] has data now", cacheName, key);
        return Optional.of(result);
      }
      return Optional.empty();
    } else {
      return Optional.empty();
    }
  }

  public <K, T> Optional<T> get(
      String cacheName,
      K key,
      @NonNull TypeReference<T> type,
      @NonNull DataSupplier<Optional<T>> supplier) {
    return get(cacheName, key, getRawType(type), supplier);
  }

  <T> Class<T> getRawType(@NonNull TypeReference<T> typeReference) {
    var type = typeReference.getType();
    if (type instanceof ParameterizedType) {
      return (Class<T>) ((ParameterizedType) type).getRawType();
    }
    return (Class<T>) type;
  }

  public void put(String cacheName, Object key, Object value) {
    getCache(cacheName).put(key, value);
  }

  public void evict(String cacheName) {
    evict(cacheName, true);
  }

  public void evict(String cacheName, boolean evictLocal) {
    evict(cacheName, evictLocal, null);
  }

  public void evict(String cacheName, Object key) {
    evict(cacheName, true, key);
  }

  public void evict(String cacheName, boolean evictLocal, Object key) {
    evict(cacheName, evictLocal, new Object[] { key });
  }

  public void evict(String cacheName, Object[] keys) {
    evict(cacheName, true, keys);
  }

  public void evict(String cacheName, boolean evictLocal, Object[] keys) {
    if (Objects.isNull(keys) || keys.length == 0) {
      getCache(cacheName).clear();
      if (evictLocal) {
        clearLocal(cacheName, null);
      }
    } else {
      Stream.of(keys)
          .parallel()
          .forEach(
              key -> {
                getCache(cacheName).evict(key);
                if (evictLocal) {
                  clearLocal(cacheName, key);
                }
              });
    }
  }

  public com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache(
      @NonNull MimosaCacheProperties.Caffeine caffeine) {
    var cacheBuilder = Caffeine.newBuilder();
    if (caffeine.getExpireAfterAccess() > 0) {
      cacheBuilder.expireAfterAccess(caffeine.getExpireAfterAccess(), TimeUnit.MILLISECONDS);
    }
    if (caffeine.getExpireAfterWrite() > 0) {
      cacheBuilder.expireAfterWrite(caffeine.getExpireAfterWrite(), TimeUnit.MILLISECONDS);
    }
    if (caffeine.getInitialCapacity() > 0) {
      cacheBuilder.initialCapacity(caffeine.getInitialCapacity());
    }
    if (caffeine.getMaximumSize() > 0) {
      cacheBuilder.maximumSize(caffeine.getMaximumSize());
    }
    if (caffeine.getMaximumWeight() > 0) {
      cacheBuilder.maximumWeight(caffeine.getMaximumWeight());
      cacheBuilder.weigher(
          ofNullable(caffeine.getWeigher()).map(this::newWeigher).orElse(MimosaWeigher.INSTANCE));
    }
    if (caffeine.getRefreshAfterWrite() > 0) {
      cacheBuilder.refreshAfterWrite(caffeine.getRefreshAfterWrite(), TimeUnit.MILLISECONDS);
    }
    if (TRUE.equals(caffeine.getWeakKeys())) {
      cacheBuilder.weakKeys();
    }
    if (TRUE.equals(caffeine.getWeakValues())) {
      cacheBuilder.weakValues();
    }
    if (TRUE.equals(caffeine.getSoftValues())) {
      cacheBuilder.softValues();
    }
    return cacheBuilder.build();
  }

  @SneakyThrows
  private Weigher newWeigher(@NonNull Class<Weigher> weigherClass) {
    return weigherClass.getDeclaredConstructor().newInstance();
  }

  @Override
  public Collection<String> getCacheNames() {
    return unmodifiableSet(this.cacheMap.keySet());
  }

  public void clearLocal(String cacheName, Object key) {
    var cache = cacheMap.get(cacheName);
    if (Objects.isNull(cache)) {
      return;
    }

    var roseCache = (MimosaCache) cache;
    roseCache.clearLocal(key);
  }
}
