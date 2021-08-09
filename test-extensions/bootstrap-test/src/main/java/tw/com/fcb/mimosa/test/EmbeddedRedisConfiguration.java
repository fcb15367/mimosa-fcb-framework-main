package tw.com.fcb.mimosa.test;

import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

/** @author Matt Ho */
@Slf4j
@TestConfiguration
@RequiredArgsConstructor
public class EmbeddedRedisConfiguration {

  // 整個 test chain 只需要一個 embedded redis, 因此我們放在 static
  private static volatile RedisServer server;

  @Value("${spring.redis.port:6379}")
  private int port;

  @Value("${spring.redis.password:}")
  private String password;

  @Synchronized
  @PreDestroy
  public void destroy() {
    if (server != null && server.isActive()) {
      server.stop();
    }
  }

  @Synchronized
  @PostConstruct
  public void afterPropertiesSet() {
    if (server != null && server.isActive()) {
      log.debug("skip starting redis server because it is already running...");
      return;
    }
    try {
      var builder = RedisServer.builder().port(this.port);
      if (!StringUtils.isEmpty(password)) {
        builder.setting("requirepass " + password);
      }
      server = builder.build();
      server.start();
      log.info("Successfully started embedded redis server on {} port", this.port);
    } catch (Exception e) {
      // 真的啟動失敗似乎也不是一個絕對會有什麼影響的問題, 畢竟 test 也不一定會用!? 就先印 warn 吧
      log.warn("Failed to start embedded redis server: {}", getRootCauseMessage(e));
    }
  }
}
