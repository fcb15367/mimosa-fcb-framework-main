package tw.com.fcb.mimosa.ext.cache.support.lock;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 標記為 Lock 鎖
 *
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
@Inherited
public @interface MimosaLock {

  /**
   * 可用 SpEL 傳入方法參數
   */
  String lockKey() default "";

  /**
   * 等待取鎖的時間，單位秒，預設 1 秒
   */
  int waitingTime() default 1;
}
