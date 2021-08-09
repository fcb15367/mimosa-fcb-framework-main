package tw.com.fcb.mimosa.tracing;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 時間監控集合 ({@code Traced collection}) 控制器
 *
 * <p>
 * <b>注意! TraceManager 不支援並行的情境 (如 parallel stream 或使用 future 等), 將視各個執行緒時為獨立追蹤個體</b>
 *
 * @see TracedCloseable
 * @author Matt Ho
 */
@Slf4j
@NoArgsConstructor(access = PRIVATE)
public final class TraceManager {

  private static final ThreadLocal<Traces> tasks = new NamedThreadLocal<>("Traced collections");

  /**
   * 初始化時間監控任務控制器
   *
   * @param id
   * @param timeUnit
   * @throws IllegalStateException if Traced collections is already active
   */
  public static void init(String id, @NonNull TimeUnit timeUnit) throws IllegalStateException {
    if (isActive()) {
      throw new IllegalStateException("Cannot activate Traced collections - already active");
    }
    log.trace("Initializing Traced collections");
    tasks.set(new Traces(id, timeUnit));
  }

  /**
   * 增加一個時間監控, 並順便建立 opentracing span
   *
   * @param name
   * @throws IllegalStateException if Traced collections is not active
   * @return
   */
  public static TracedCloseable traced(@NonNull String name) throws IllegalStateException {
    return traced(name, true, null);
  }

  /**
   * 增加一個時間監控, 並順便建立 opentracing span
   *
   * @param name
   * @param tags 建立 span 要順便帶入的 tags 資訊
   * @throws IllegalStateException if Traced collections is not active
   * @return
   */
  public static TracedCloseable traced(@NonNull String name, @Nullable Map<String, String> tags)
      throws IllegalStateException {
    return traced(name, true, tags);
  }

  /**
   * 增加一個時間監控, 並順便建立 opentracing span
   *
   * @param name
   * @param buildSpan 是否要建立 opentracing span
   * @throws IllegalStateException if Traced collections is not active
   * @return
   */
  public static TracedCloseable traced(@NonNull String name, boolean buildSpan)
      throws IllegalStateException {
    return traced(name, buildSpan, null);
  }

  /**
   * 增加一個時間監控
   *
   * @param name
   * @param buildSpan 是否要建立 opentracing span
   * @param tags 建立 span 要順便帶入的 tags 資訊
   * @throws IllegalStateException if Traced collections is not active
   * @return
   */
  public static TracedCloseable traced(
      @NonNull String name, boolean buildSpan, @Nullable Map<String, String> tags)
      throws IllegalStateException {
    if (!isActive()) {
      throw new IllegalStateException("Cannot add task to Traced collections - not active");
    }
    var task = new TracedCloseable(name, buildSpan, ofNullable(tags).orElseGet(Collections::emptyMap));
    tasks.get().add(task);
    return task;
  }

  public static Optional<Traces> get() {
    return ofNullable(tasks.get());
  }

  public static boolean isActive() {
    return tasks.get() != null;
  }

  public static void clear() {
    tasks.remove();
  }
}
