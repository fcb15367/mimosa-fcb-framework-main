package tw.com.fcb.mimosa.ext.data.jpa.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "mimosa.data.jpa")
public class MimosaDataJpaProperties {

  private Log4jdbc log4jdbc = new Log4jdbc();

  @Data
  public static class Log4jdbc {

    private boolean enabled = true;
  }
}
