package tw.com.fcb.mimosa.examples.loggingjson;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/** @author Matt Ho */
@MimosaBootstrap
public class LoggingJsonApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(LoggingJsonApplication.class, args);
  }

  @Slf4j
  @Component
  static class WriteSomeLog implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
      log.info("this is my log message"); // 查看 console
    }
  }
}
