package tw.com.fcb.mimosa.ext.data.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;

import com.zaxxer.hikari.HikariDataSource;

import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
public class HikariLog4jdbcBeanPostProcessor extends Log4jdbcBeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof HikariDataSource) {
      return new WrappedSpyDataSource((DataSourceSpy) super.postProcessBeforeInitialization(bean, beanName),
          (HikariDataSource) bean);
    } else {
      return bean;
    }
  }

  @AllArgsConstructor
  private class WrappedSpyDataSource extends HikariDataSource {

    @Delegate(types = { DataSource.class, Spy.class })
    DataSourceSpy dataSourceSpy;

    @Delegate(types = HikariDataSource.class, excludes = DataSource.class)
    HikariDataSource original;
  }
}
