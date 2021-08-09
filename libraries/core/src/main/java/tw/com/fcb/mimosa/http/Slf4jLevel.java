package tw.com.fcb.mimosa.http;

import static java.lang.Integer.MAX_VALUE;

import org.jooq.lambda.function.Consumer3;
import org.slf4j.Logger;
import org.slf4j.event.Level;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** @author Matt Ho */
@RequiredArgsConstructor
public enum Slf4jLevel {
  TRACE(Level.TRACE.toInt(), (log, e, t) -> log.trace("{}", e.shortSummary(), t)),
  DEBUG(Level.DEBUG.toInt(), (log, e, t) -> log.debug("{}", e.shortSummary(), t)),
  INFO(Level.INFO.toInt(), (log, e, t) -> log.info("{}", e.shortSummary(), t)),
  WARN(Level.WARN.toInt(), (log, e, t) -> log.warn("{}", e.shortSummary(), t)),
  ERROR(Level.ERROR.toInt(), (log, e, t) -> log.error("{}", e.shortSummary(), t)),
  OFF(MAX_VALUE, (log, e, t) -> {
  }),
  ;

  @Getter
  final int level;

  @Getter
  @NonNull
  final Consumer3<Logger, APIError, Throwable> logger;
}
