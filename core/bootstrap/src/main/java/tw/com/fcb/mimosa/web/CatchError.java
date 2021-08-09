package tw.com.fcb.mimosa.web;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.function.BinaryOperator.minBy;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static tw.com.fcb.mimosa.web.CatchError.LoggingLevel.OverlayStrategy.HIGHEST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.http.APIError;
import tw.com.fcb.mimosa.http.Slf4jLevel;
import tw.com.fcb.mimosa.http.SpecifiedLoggingLevel;

/** @author Matt Ho */
@Slf4j
@Data
public class CatchError {
  Collection<String> classes = new ArrayList<>();
  Collection<String> codes = new ArrayList<>();
  Logging logging = new Logging();

  public Collection<Class> getClassesForName() {
    return classes.stream()
        .map(
            name -> {
              try {
                return ClassUtils.getClass(name);
              } catch (Exception e) {
                log.warn("Ignored web-error-catch-class: {} for reason: {}", name, getRootCauseMessage(e));
                return null;
              }
            })
        .filter(Objects::nonNull)
        .collect(toList());
  }

  @Data
  public static class Logging {
    LoggingLevel level = new LoggingLevel();
  }

  @Data
  public static class LoggingLevel {

    @NonNull
    Slf4jLevel defaultLevel = Slf4jLevel.ERROR;

    @NonNull
    Map<Slf4jLevel, Collection<String>> exact = new HashMap<>();

    /** 避免設定錯誤就造成 Spring 啟動失敗, 因此先以文字接設定, runtime 時再進行 Pattern compile */
    @NonNull
    @ToString.Exclude
    Map<Slf4jLevel, Collection<String>> regex = new HashMap<>();

    private Map<Slf4jLevel, Collection<Pattern>> pattern = new HashMap<>();

    @NonNull
    OverlayStrategy overlayStrategy = HIGHEST;

    LoggingLevel compile() {
      regex.forEach(
          (k, v) -> pattern.put(
              k,
              v.stream()
                  .map(
                      r -> {
                        try {
                          return Pattern.compile(r);
                        } catch (Exception e) {
                          log.warn(
                              "Ignored web-error-logging-level-regex: {}/{} for reason: {}",
                              k,
                              r,
                              getRootCauseMessage(e));
                          return null;
                        }
                      })
                  .filter(Objects::nonNull)
                  .collect(toList())));
      return this;
    }

    void log(@NonNull Logger logger, @NonNull APIError error, @Nullable Throwable t) {
      var level = findLevel(error.getCode(), t);
      log.trace("logging message for level: {} ", level);
      level.getLogger().accept(logger, error, t);
    }

    Slf4jLevel findLevel(@NonNull String code, @Nullable Throwable t) {
      return findSpecifiedLevel(t) // 檢查例外是否有指定
          .or(() -> findExactLevel(code)) // 找 exact 的設定
          .or(() -> findRegexLevel(code)) // 找 regex 的設定
          .orElse(defaultLevel);
    }

    Optional<Slf4jLevel> findSpecifiedLevel(@Nullable Throwable t) {
      return ofNullable(t)
          .filter(SpecifiedLoggingLevel.class::isInstance)
          .map(SpecifiedLoggingLevel.class::cast)
          .map(SpecifiedLoggingLevel::getLoggingLevel);
    }

    Optional<Slf4jLevel> findExactLevel(@NonNull String code) {
      return exact.entrySet().stream()
          .filter(entry -> entry.getValue().stream().anyMatch(code::equals))
          .map(Entry::getKey)
          .reduce(overlayStrategy.operator);
    }

    Optional<Slf4jLevel> findRegexLevel(@NonNull String code) {
      return pattern.entrySet().stream()
          .filter(entry -> entry.getValue().stream().anyMatch(p -> p.matcher(code).matches()))
          .map(Entry::getKey)
          .reduce(overlayStrategy.operator);
    }

    @RequiredArgsConstructor
    public enum OverlayStrategy {
      HIGHEST(maxBy(comparing(Slf4jLevel::getLevel))),
      LOWEST(minBy(comparing(Slf4jLevel::getLevel))),
      ;

      @NonNull
      final BinaryOperator<Slf4jLevel> operator;
    }
  }
}
