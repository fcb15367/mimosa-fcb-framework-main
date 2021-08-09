package tw.com.fcb.mimosa.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.ContextLoader;

import tw.com.fcb.mimosa.bootstrap.MimosaBanner;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
public class MimosaTestContextBootstrapper extends SpringBootTestContextBootstrapper {

  static class MimosaContextLoader extends SpringBootContextLoader {

    @Override
    protected SpringApplication getSpringApplication() {
      var application = new SpringApplication();
      application.setBanner(new MimosaBanner());
      return application;
    }
  }

  @Override
  protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
    return MimosaContextLoader.class;
  }
}
