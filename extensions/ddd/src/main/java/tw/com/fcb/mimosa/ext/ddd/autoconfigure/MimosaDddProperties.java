package tw.com.fcb.mimosa.ext.ddd.autoconfigure;

import static lombok.AccessLevel.PRIVATE;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
@ConfigurationProperties(prefix = "mimosa.ddd")
public class MimosaDddProperties {

  Application application = new Application();

  @Data
  public static class Application {
    /** UseCase 調度器 */
    UseCaseDispatcher useCaseDispatcher = new UseCaseDispatcher();
  }

  @Data
  public static class UseCaseDispatcher {
    /** 啟動 UseCase 調度器 */
    boolean enabled = true;
  }
}
