package tw.com.fcb.mimosa.test.util;

import java.lang.reflect.Field;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import io.opentracing.Tracer;
import io.opentracing.noop.NoopTracerFactory;
import io.opentracing.util.GlobalTracer;
import lombok.experimental.UtilityClass;

/**
 * 由於 {@link GlobalTracer#registerIfAbsent(Tracer)} 方法的實作方式只能被建構一次, 這樣可能會讓一些 unit test chain 測試失敗, 又
 * {@code GlobalTracer} 本身沒又提供 reset 的方式, 所以此 Util 主要就在幫助你重置 {@code GlobalTracer}.
 *
 * <p>
 * 你可以在 unit test 中的 {@link BeforeEach}, {@link BeforeAll}, {@link AfterEach} 或 {@link AfterAll}
 * 區塊中來執行, 如:
 *
 * <p>
 *
 * <pre>
 * {@code
 * &#64;BeforeAll
 * &#64;AfterAll
 * static void reset() {
 *   GlobalTracerTestUtil.resetGlobalTracer();
 * }
 * }
 * </pre>
 *
 * @see <a href=
 *      "https://github.com/opentracing/opentracing-java/issues/170">https://github.com/opentracing/opentracing-java/issues/170</a>
 * @see <a href=
 *      "https://github.com/opentracing/opentracing-java/blob/master/opentracing-util/src/test/java/io/opentracing/util/GlobalTracerTestUtil.java">https://github.com/opentracing/opentracing-java/blob/master/opentracing-util/src/test/java/io/opentracing/util/GlobalTracerTestUtil.java</a>
 * @author Matt Ho
 */
@UtilityClass
public class GlobalTracerTestUtil {

  /** Resets the {@link GlobalTracer} to its initial, unregistered state. */
  public void resetGlobalTracer() {
    setGlobalTracerUnconditionally(NoopTracerFactory.create());

    try {
      Field isRegisteredField = GlobalTracer.class.getDeclaredField("isRegistered");
      isRegisteredField.setAccessible(true);
      isRegisteredField.set(null, false);
      isRegisteredField.setAccessible(false);
    } catch (Exception e) {
      throw new IllegalStateException("Error reflecting GlobalTracer.tracer: " + e.getMessage(), e);
    }
  }

  /**
   * Unconditionally sets the {@link GlobalTracer} to the specified {@link Tracer tracer} instance.
   *
   * @param tracer The tracer to become the GlobalTracer's delegate.
   */
  public void setGlobalTracerUnconditionally(Tracer tracer) {
    try {
      Field globalTracerField = GlobalTracer.class.getDeclaredField("tracer");
      globalTracerField.setAccessible(true);
      globalTracerField.set(null, tracer);
      globalTracerField.setAccessible(false);

      Field isRegisteredField = GlobalTracer.class.getDeclaredField("isRegistered");
      isRegisteredField.setAccessible(true);
      isRegisteredField.set(null, true);
      isRegisteredField.setAccessible(false);
    } catch (Exception e) {
      throw new IllegalStateException("Error reflecting GlobalTracer.tracer: " + e.getMessage(), e);
    }
  }
}
