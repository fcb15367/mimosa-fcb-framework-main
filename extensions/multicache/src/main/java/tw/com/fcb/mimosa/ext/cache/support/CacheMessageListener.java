package tw.com.fcb.mimosa.ext.cache.support;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.SerializationException;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
@Builder
public class CacheMessageListener implements MessageListener {

  private RedisTemplate<String, Object> redisTemplate;
  private MimosaCacheManager cacheManager;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      var serializer = (JacksonJsonSerializer) redisTemplate.getValueSerializer();
      var cacheMessage = (CacheMessage) serializer.deserialize(message.getBody());
      log.debug(
          "received a redis topic message from [{}], clearing local cache for cache name: [{}], key: [{}]",
          new String(message.getChannel()),
          cacheMessage.getCacheName(),
          cacheMessage.getKey());
      cacheManager.clearLocal(cacheMessage.getCacheName(), cacheMessage.getKey());
    } catch (SerializationException e) {
      log.trace("{}", e.getMessage(), e);
    }
  }
}
