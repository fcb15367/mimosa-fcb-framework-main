package tw.com.fcb.mimosa.ext.cache.support.lock;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.redis.util.RedisLockRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 分散式鎖 Aspect
 *
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class MimosaLockAspect {

  private final RedisLockRegistry redisLockRegistry;

  @Pointcut("@annotation(tw.com.fcb.mimosa.ext.cache.support.lock.MimosaLock)")
  public void lockPointCut() {
  }

  @Around("lockPointCut()")
  public Object roseLock(ProceedingJoinPoint joinPoint) {
    Object result = null;

    var signature = (MethodSignature) joinPoint.getSignature();
    var method = signature.getMethod();
    var arguments = joinPoint.getArgs();
    var roseLock = method.getAnnotation(MimosaLock.class);

    try {
      var lockKey = parseSpel(roseLock.lockKey(), method, arguments);
      var lock = redisLockRegistry.obtain(lockKey);

      try {
        boolean acquired = lock.tryLock(roseLock.waitingTime(), TimeUnit.SECONDS);
        if (acquired) {
          result = joinPoint.proceed();
        } else {
          log.debug("Thread[{}] 未取到鎖，目前鎖的狀態：{}", Thread.currentThread().getName(), lock);
          throw new IllegalStateException("系統正在執行中，請勿重複執行");
        }
      } catch (CannotAcquireLockException e) {
        log.debug(e.getLocalizedMessage());
        throw e;
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      } finally {
        log.debug("解鎖：{}", lockKey);
        try {
          lock.unlock();
          log.debug("{} 鎖已釋放", lockKey);
        } catch (Exception e) {
          log.error("解鎖出錯：{}", e.getMessage(), e);
        }
      }
    } catch (CannotAcquireLockException e) {
      log.error(e.getLocalizedMessage());
      throw e;
    } catch (Throwable e) {
      log.error("distributed lock error: {}", e.getLocalizedMessage());
    }
    return result;
  }

  public static Object parseSpel(String key, Method method, Object[] args) {
    var parser = new SpelExpressionParser();
    var parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    var parameterNames = parameterNameDiscoverer.getParameterNames(method);
    var context = new StandardEvaluationContext();
    if (args.length == parameterNames.length) {
      for (int i = 0, len = args.length; i < len; i++) {
        context.setVariable(parameterNames[i], args[i]);
      }
    }
    return parser.parseExpression(key).getValue(context);
  }
}
