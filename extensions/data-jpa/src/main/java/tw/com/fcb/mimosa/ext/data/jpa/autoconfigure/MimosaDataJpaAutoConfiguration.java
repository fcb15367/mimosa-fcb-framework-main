package tw.com.fcb.mimosa.ext.data.jpa.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import tw.com.fcb.mimosa.ext.data.jpa.HikariLog4jdbcBeanPostProcessor;
import tw.com.fcb.mimosa.ext.data.jpa.Log4jdbcBeanPostProcessor;
import tw.com.fcb.mimosa.ext.data.jpa.SpringSecurityAuditorAware;

/**
 * @author Matt Ho
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DataSourceSpy.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(MimosaDataJpaProperties.class)
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaAuditing
public class MimosaDataJpaAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public AuditorAware<String> springSecurityAuditorAware() {
    return new SpringSecurityAuditorAware();
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(value = "mimosa.data.jpa.log4jdbc.enabled", matchIfMissing = true)
  public static class Log4jdbcConfiguration {

    @Bean
    @ConditionalOnMissingClass("com.zaxxer.hikari.HikariDataSource")
    @ConditionalOnMissingBean(Log4jdbcBeanPostProcessor.class)
    public Log4jdbcBeanPostProcessor log4jdbcBeanPostProcessor() {
      return new Log4jdbcBeanPostProcessor();
    }

    @Bean
    @ConditionalOnClass(HikariDataSource.class)
    @ConditionalOnMissingBean(HikariLog4jdbcBeanPostProcessor.class)
    public HikariLog4jdbcBeanPostProcessor hikariLog4jdbcBeanPostProcessor() {
      return new HikariLog4jdbcBeanPostProcessor();
    }
  }
}
