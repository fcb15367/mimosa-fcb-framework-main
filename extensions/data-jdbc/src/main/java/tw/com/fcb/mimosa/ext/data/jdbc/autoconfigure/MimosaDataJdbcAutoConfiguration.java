package tw.com.fcb.mimosa.ext.data.jdbc.autoconfigure;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jdbc.repository.config.MyBatisJdbcConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import tw.com.fcb.mimosa.ext.data.jdbc.HikariLog4jdbcBeanPostProcessor;
import tw.com.fcb.mimosa.ext.data.jdbc.Log4jdbcBeanPostProcessor;
import tw.com.fcb.mimosa.ext.data.jdbc.MimosaMyBatisSqlInterceptor;
import tw.com.fcb.mimosa.ext.data.jdbc.MimosaNamedParameterJdbcTemplate;
import tw.com.fcb.mimosa.ext.data.jdbc.RepositoryAspect;
import tw.com.fcb.mimosa.ext.data.jdbc.SpringSecurityAuditorAware;
import tw.com.fcb.mimosa.ext.data.jdbc.event.AfterProceedCallback;

/** @author Matt Ho */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DataSourceSpy.class)
@AutoConfigureBefore(JdbcTemplateAutoConfiguration.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(MimosaDataJdbcProperties.class)
@EnableJdbcRepositories
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJdbcAuditing
@Import(MyBatisJdbcConfiguration.class)
public class MimosaDataJdbcAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public AuditorAware<String> springSecurityAuditorAware() {
    return new SpringSecurityAuditorAware();
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnProperty(value = "mimosa.data.jdbc.log4jdbc.enabled", matchIfMissing = true)
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

  @Configuration(proxyBeanMethods = false)
  public static class MimosaJdbcTemplateConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RepositoryAspect repositoriesAspect(
        ApplicationContext context, Collection<AfterProceedCallback> callbacks) {
      return new RepositoryAspect(
          context,
          callbacks.stream().collect(Collectors.groupingBy(AfterProceedCallback::get_type)));
    }

    @Bean
    @ConditionalOnMissingBean(NamedParameterJdbcOperations.class)
    NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
      return new MimosaNamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    MimosaMyBatisSqlInterceptor mimosaMyBatisSqlInterceptor() {
      return new MimosaMyBatisSqlInterceptor();
    }
  }
}
