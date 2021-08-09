package tw.com.fcb.mimosa.ext.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import tw.com.fcb.mimosa.ext.cache.support.MimosaCacheManager;

/** @author Matt Ho */
@AllArgsConstructor
class MimosaCachingConfigurerSupport extends CachingConfigurerSupport {

  @NonNull
  final PrefixedKeyGenerator keyGenerator;

  @NonNull
  final MimosaCacheManager cacheManager;

  @Override
  public KeyGenerator keyGenerator() {
    return keyGenerator;
  }

  @Override
  public CacheManager cacheManager() {
    return cacheManager;
  }
}
