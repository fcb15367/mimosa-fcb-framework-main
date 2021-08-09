package tw.com.fcb.mimosa.web;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;
import static java.util.Optional.ofNullable;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import tw.com.fcb.mimosa.http.APIError;

/**
 * 代表在 REST API 的呼叫過程中, 被 AOP 抓到的 Throwable
 *
 * @author Matt Ho
 */
@Getter
@ToString
public class ErrorCaughtEvent extends ApplicationEvent {

  final transient Optional<Throwable> throwing;

  @Builder
  ErrorCaughtEvent(@NonNull APIError error, @Nullable Throwable throwing) {
    super(error);
    this.throwing = ofNullable(throwing);
  }

  /** @return 捕捉到的 {@link APIError} 物件 */
  public APIError getError() {
    return (APIError) source;
  }

  /** @return 捕捉到的時間 */
  public final LocalDateTime getCaughtTime() {
    return ofInstant(ofEpochMilli(getTimestamp()), systemDefault());
  }
}
