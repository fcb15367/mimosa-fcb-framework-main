package tw.com.fcb.mimosa.t9n;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tw.com.fcb.mimosa.domain.t9n.TranslationService;

/** @author Matt Ho */
@Configuration(proxyBeanMethods = false)
public class TranslationAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  TranslationService translationService() {
    return new NoOpTranslationService();
  }
}
