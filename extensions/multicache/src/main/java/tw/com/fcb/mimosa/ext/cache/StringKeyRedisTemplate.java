package tw.com.fcb.mimosa.ext.cache;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import lombok.NonNull;

/**
 * Mimosa 使用的 string key redis-template, 有這支 class 可以方便使用的人透過 constructor 注入
 *
 * @author Matt Ho
 */
public class StringKeyRedisTemplate extends RedisTemplate<String, Object> {

  public StringKeyRedisTemplate(
      @NonNull RedisSerializer<?> serializer, @NonNull RedisConnectionFactory connectionFactory) {
    this.setDefaultSerializer(serializer);
    this.setValueSerializer(serializer);
    this.setHashValueSerializer(serializer);
    this.setHashKeySerializer(serializer);

    this.setKeySerializer(RedisSerializer.string());
    this.setConnectionFactory(connectionFactory);
  }
}
