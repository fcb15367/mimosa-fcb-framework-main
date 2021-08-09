package tw.com.fcb.mimosa.ext.ddd.application;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 為了不想讓專案組可以 import 到不需要知道的的 class, 因此配置 config 必須要在同 package 下, 這樣我們才能不開成 public
 *
 * @author Matt Ho
 */
public class UseCaseConfiguration {

  @Bean
  @ConditionalOnProperty(value = "mimosa.ddd.application.use-case-dispatcher.enabled", matchIfMissing = true)
  @ConditionalOnMissingBean
  UseCases useCases(ListableBeanFactory beanFactory) {
    return new SpringBeanUseCases(beanFactory);
  }
}
