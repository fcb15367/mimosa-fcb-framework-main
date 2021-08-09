package tw.com.fcb.mimosa.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

import javax.annotation.PostConstruct;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.opentracing.Tracer;
import io.opentracing.contrib.spring.cloud.async.DefaultAsyncAutoConfiguration;
import io.opentracing.contrib.spring.cloud.async.instrument.TracedThreadPoolTaskExecutor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.bootstrap.BootstrapAutoConfiguration;
import tw.com.fcb.mimosa.bootstrap.MimosaConfigProperties;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(BootstrapAutoConfiguration.class)
@AutoConfigureBefore({ DefaultAsyncAutoConfiguration.class, TaskExecutionAutoConfiguration.class })
public class MimosaAsyncAutoConfiguration implements AsyncConfigurer {

  private final Environment environment;
  private final MimosaConfigProperties properties;
  private final Tracer tracer;
  private AsyncProperties async;
  @Getter
  private Executor asyncExecutor;

  @PostConstruct
  public void init() {
    this.async = properties.getAsync();

    var executor = new ThreadPoolTaskExecutor();
    var threadNamePrefix = environment.getProperty("spring.application.name", "unknown-app");
    executor.setThreadNamePrefix(threadNamePrefix + "-");
    executor.setCorePoolSize(async.corePoolSize);
    executor.setMaxPoolSize(async.maxPoolSize);
    executor.setKeepAliveSeconds(async.keepAliveSeconds);
    executor.setQueueCapacity(async.queueCapacity);
    executor.setAllowCoreThreadTimeOut(async.allowCoreThreadTimeout);
    executor.setRejectedExecutionHandler(new CallerRunsPolicy());
    executor.initialize();
    this.asyncExecutor = new TracedThreadPoolTaskExecutor(tracer, executor);
  }
}
