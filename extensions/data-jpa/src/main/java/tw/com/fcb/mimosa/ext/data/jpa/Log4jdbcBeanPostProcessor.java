package tw.com.fcb.mimosa.ext.data.jpa;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;

import net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
public class Log4jdbcBeanPostProcessor implements BeanPostProcessor {

  @Autowired
  private Environment environment;

  private static final String[] PROPERTIES_TO_COPY = {
      "log4jdbc.log4j2.properties.file",
      "log4jdbc.debug.stack.prefix",
      "log4jdbc.sqltiming.warn.threshold",
      "log4jdbc.sqltiming.error.threshold",
      "log4jdbc.dump.booleanastruefalse",
      "log4jdbc.dump.fulldebugstacktrace",
      "log4jdbc.dump.sql.maxlinelength",
      "log4jdbc.statement.warn",
      "log4jdbc.dump.sql.select",
      "log4jdbc.dump.sql.insert",
      "log4jdbc.dump.sql.update",
      "log4jdbc.dump.sql.delete",
      "log4jdbc.dump.sql.create",
      "log4jdbc.dump.sql.addsemicolon",
      "log4jdbc.auto.load.popular.drivers",
      "log4jdbc.drivers",
      "log4jdbc.trim.sql",
      "log4jdbc.trim.sql.extrablanklines",
      "log4jdbc.suppress.generated.keys.exception",
      "log4jdbc.log4j2.properties.file",
  };

  @PostConstruct
  public void init() {
    Arrays.stream(PROPERTIES_TO_COPY)
        .filter(property -> environment.containsProperty(property))
        .forEach(property -> System.setProperty(property, environment.getProperty(property)));

    System.setProperty("log4jdbc.spylogdelegator.name",
        this.environment.getProperty("log4jdbc.spylogdelegator.name", Slf4jSpyLogDelegator.class.getName()));
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof DataSource) {
      return new DataSourceSpy((DataSource) bean);
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }
}
