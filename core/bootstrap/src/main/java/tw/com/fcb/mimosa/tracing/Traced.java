package tw.com.fcb.mimosa.tracing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * 標記要被監控, 所有監控都會被 {@code TracedAspect} 所紀錄
 *
 * <p>
 * <b>注意! Traced 不支援並行的情境 (如 parallel stream 或使用 future 等), 將視各個執行緒時為獨立追蹤個體</b>
 *
 * @see TracedAspect
 * @author Matt Ho
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Traced {

  /**
   * The name of the timer.
   *
   * @return The name of the timer.
   */
  @AliasFor("name")
  String value() default "";

  /**
   * The name of the timer.
   *
   * @return The name of the timer.
   */
  @AliasFor("value")
  String name() default "";

  /**
   * 是否要建立 opentracing span
   *
   * @return
   */
  boolean buildSpan() default true;

  /**
   * @return The tags of the timer. Each {@code String} tag must be in the form of 'key=value'. If
   *         the input is empty or does not contain a '=' sign, the entry is ignored.
   */
  String[] tags() default {};
}
