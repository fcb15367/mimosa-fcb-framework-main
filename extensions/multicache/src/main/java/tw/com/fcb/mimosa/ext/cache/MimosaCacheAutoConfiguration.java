package tw.com.fcb.mimosa.ext.cache;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.actuator.MimosaMetricsAutoConfiguration;
import tw.com.fcb.mimosa.bootstrap.BootstrapAutoConfiguration;
import tw.com.fcb.mimosa.ext.cache.support.CacheMessageListener;
import tw.com.fcb.mimosa.ext.cache.support.JacksonJsonSerializer;
import tw.com.fcb.mimosa.ext.cache.support.MimosaCacheManager;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({
    BootstrapAutoConfiguration.class,
    MimosaMetricsAutoConfiguration.class,
    RedisAutoConfiguration.class
})
@EnableConfigurationProperties(MimosaCacheProperties.class)
@EnableCaching(proxyTargetClass = true)
@Import(MimosaCachingConfigurerSupport.class)
@ConditionalOnProperty(value = "mimosa.cache.enabled", matchIfMissing = true)
@Slf4j
public class MimosaCacheAutoConfiguration {

  private final Environment environment;
  private final MimosaCacheProperties cacheProperties;

  @Bean
  PrefixedKeyGenerator prefixedKeyGenerator() {
    return new PrefixedKeyGenerator(cacheProperties.getCachePrefix());
  }

  @Bean(MimosaCacheManager.CACHE_MANAGER_NAME)
  public MimosaCacheManager cacheManager(
      StringKeyRedisTemplate template,
      ObjectProvider<CacheMetricsRegistrar> cacheMetricsRegistrarProvider) {
    return MimosaCacheManager.builder()
        .cacheProperties(cacheProperties)
        .stringKeyRedisTemplate(template)
        .cacheMetricsRegistrarProvider(cacheMetricsRegistrarProvider)
        .build();
  }

  @Bean
  StringKeyRedisTemplate stringKeyRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return new StringKeyRedisTemplate(new JacksonJsonSerializer(), redisConnectionFactory);
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListenerContainer(
      RedisConnectionFactory redisConnectionFactory,
      StringKeyRedisTemplate template,
      MimosaCacheManager cacheManager) {
    var container = new RedisMessageListenerContainer();
    container.setConnectionFactory(redisConnectionFactory);
    container.addMessageListener(
        CacheMessageListener.builder().redisTemplate(template).cacheManager(cacheManager).build(),
        cacheProperties.getRedis().getChannelTopics());
    return container;
  }

  @Bean
  public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
    var applicationName = environment.getProperty("spring.application.name");
    var registryKey = StringUtils.isEmpty(applicationName) ? "softleader-rose-application" : applicationName;
    return new RedisLockRegistry(redisConnectionFactory, registryKey);
  }
}
