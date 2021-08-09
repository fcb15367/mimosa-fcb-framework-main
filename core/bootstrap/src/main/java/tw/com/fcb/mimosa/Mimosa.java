package tw.com.fcb.mimosa;

import static java.lang.String.format;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.bootstrap.MimosaBanner;

/**
 * 服務啟動器
 *
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
public final class Mimosa {

  private static final String PID_PATH = "mimosa.pid.path";

  private Mimosa() {
  }

  public static ConfigurableApplicationContext bootstrap(Class<?> source, String[] args) {
    var app = getSpringApplication(source);
    return app.run(args);
  }

  private static SpringApplication getSpringApplication(Class<?>... sources) {
    var app = new SpringApplication(sources);
    setApp(app);
    return app;
  }

  private static void setApp(SpringApplication app) {
    app.setBanner(new MimosaBanner());
    app.addListeners(new ApplicationPidFileWriter(pid()));
  }

  /**
   *
   * @return
   */
  private static String pid() {
    return Path.of(System.getProperty(PID_PATH, "./bin"), format("app%s.pid", hostname()))
        .toString();
  }

  private static String hostname() {
    try {
      return "-" + InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return "";
    }
  }
}
