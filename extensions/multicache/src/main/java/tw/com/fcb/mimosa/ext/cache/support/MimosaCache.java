package tw.com.fcb.mimosa.ext.cache.support;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.ext.cache.MimosaCacheProperties;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
public class MimosaCache extends AbstractValueAdaptingCache {

  private String name;
  private RedisTemplate<String, Object> stringKeyRedisTemplate;
  private Cache<Object, Object> caffeineCache;
  private String cachePrefix = "";
  private long defaultExpiration = 0;
  private Map<String, Long> expires = Maps.newHashMap();
  private String topic = "mimosa:cache:redis:caffeine:topic";

  private Map<String, ReentrantLock> keyLockMap = new ConcurrentHashMap<>();

  protected MimosaCache(boolean allowNullValues) {
    super(allowNullValues);
  }

  public MimosaCache(
      String name,
      RedisTemplate<String, Object> stringKeyRedisTemplate,
      Cache<Object, Object> caffeineCache,
      MimosaCacheProperties cacheProperties,
      MimosaCacheProperties.Redis redisConfig) {
    this(cacheProperties.isCacheNullValues());
    this.name = name;
    this.stringKeyRedisTemplate = stringKeyRedisTemplate;
    this.caffeineCache = caffeineCache;
    this.cachePrefix = cacheProperties.getCachePrefix();
    this.defaultExpiration = redisConfig.getDefaultExpiration();
    this.expires = redisConfig.getExpires();
    this.topic = redisConfig.determineTopic(name);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Object getNativeCache() {
    return this;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    var value = lookup(key);
    if (Objects.nonNull(value)) {
      return (T) value;
    }

    var lock = keyLockMap.get(key.toString());
    if (Objects.isNull(lock)) {
      log.trace("creating lock for key: {}", key);
      lock = new ReentrantLock();
      keyLockMap.putIfAbsent(key.toString(), lock);
    }

    try {
      lock.lock();
      value = lookup(key);
      if (Objects.nonNull(value)) {
        return (T) value;
      }

      value = valueLoader.call();
      var storeValue = toStoreValue(value);
      put(key, storeValue);
      return (T) value;
    } catch (Exception e) {
      throw new ValueRetrievalException(key, valueLoader, e.getCause());
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void put(Object key, Object value) {
    if (!super.isAllowNullValues() && Objects.isNull(value)) {
      this.evict(key);
      return;
    }
    setIfAbsent(key, value);
    caffeineCache.put(key, value);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    var cacheKey = getKey(key);
    Object prevValue = null;

    synchronized (key) {
      try {
        prevValue = stringKeyRedisTemplate.opsForValue().get(cacheKey);
      } catch (Exception e) {
        log.warn(e.getMessage());
      }
      if (Objects.isNull(prevValue)) {
        setIfAbsent(key, value);
        caffeineCache.put(key, toStoreValue(value));
      }
    }
    return toValueWrapper(prevValue);
  }

  private void setIfAbsent(Object key, Object value) {
    var expire = getExpire();
    try {
      if (expire > 0) {
        stringKeyRedisTemplate
            .opsForValue()
            .set(getKey(key), toStoreValue(value), expire, TimeUnit.MILLISECONDS);
      } else {
        stringKeyRedisTemplate.opsForValue().set(getKey(key), toStoreValue(value));
      }
    } catch (Exception e) {
      log.warn(e.getMessage());
    }

    push(CacheMessage.builder().cacheName(this.name).key(key).build());
  }

  @Override
  public void evict(Object key) {
    try {
      stringKeyRedisTemplate.delete(getKey(key));
    } catch (Exception e) {
      log.warn(e.getMessage());
    }

    push(CacheMessage.builder().cacheName(this.name).key(key).build());

    caffeineCache.invalidate(key);
  }

  @Override
  public void clear() {
    try {
      var keys = stringKeyRedisTemplate.keys(this.name.concat(":*"));
      keys.stream().forEach(stringKeyRedisTemplate::delete);
    } catch (Exception e) {
      log.warn(e.getMessage());
    }

    push(CacheMessage.builder().cacheName(this.name).key(null).build());

    caffeineCache.invalidateAll();
  }

  @Override
  protected Object lookup(Object key) {
    var cacheKey = getKey(key);
    var value = caffeineCache.getIfPresent(key);
    if (Objects.nonNull(value)) {
      log.trace("get cache from caffeine for key: {}", cacheKey);
      return value;
    }

    try {
      value = stringKeyRedisTemplate.opsForValue().get(cacheKey);
    } catch (Exception e) {
      log.warn(e.getMessage());
    }

    if (Objects.nonNull(value)) {
      log.trace("get cache from redis and put in caffeine for key: {}", cacheKey);
      caffeineCache.put(key, value);
    }
    return value;
  }

  private String getKey(Object key) {
    return this.name
        .concat(":")
        .concat(
            StringUtils.isEmpty(cachePrefix)
                ? key.toString()
                : cachePrefix.concat(":").concat(key.toString()));
  }

  private long getExpire() {
    var expire = defaultExpiration;
    var cacheNameExpire = expires.get(this.name);
    return Objects.nonNull(cacheNameExpire) ? cacheNameExpire.longValue() : expire;
  }

  private void push(CacheMessage message) {
    try {
      stringKeyRedisTemplate.convertAndSend(topic, message);
    } catch (Exception e) {
      log.warn(e.getMessage());
    }
  }

  public void clearLocal(Object key) {
    log.debug("clearing local cache for key: {}", key);
    if (Objects.isNull(key)) {
      caffeineCache.invalidateAll();
    } else {
      caffeineCache.invalidate(key);
    }
  }
}
