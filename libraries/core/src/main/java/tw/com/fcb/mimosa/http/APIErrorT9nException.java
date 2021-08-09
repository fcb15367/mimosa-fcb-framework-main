package tw.com.fcb.mimosa.http;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import tw.com.fcb.mimosa.http.APIErrorT9n.APIErrorT9nBuilder;

/**
 * 透過 Exception 來控制 (中斷) 程式流程, 並且指定期望的 {@code APIError}
 *
 * @see APIError
 * @author Matt Ho
 */
@Getter
public class APIErrorT9nException extends RuntimeException implements SpecifiedLoggingLevel {

  /**
   * 提供一個語法糖整合到 {@code Optional.orElseThrow(..)} 中
   *
   * <p>
   *
   * <pre>
   * import static tw.com.fcb.mimosa.http.APIErrorException.apiError;
   *
   * Optional optional = ...;
   * // ...
   * optional.orElseThrow(
   *         apiError(APIError.builder().code("ERR_E_XXX").message("something went wrong").build()));
   * </pre>
   *
   * @param error
   * @see APIErrorT9nException#apiError(UnaryOperator)
   * @return
   */
  public static Supplier<APIErrorT9nException> apiError(@NonNull APIErrorT9n error) {
    return () -> new APIErrorT9nException(error);
  }

  /** @see #apiError(APIErrorT9n) */
  public static Supplier<APIErrorT9nException> apiError(@NonNull APIErrorT9n error, Throwable cause) {
    return () -> new APIErrorT9nException(error, cause);
  }

  /**
   * 提供一個語法糖整合到 {@code Optional.orElseThrow(..)} 中
   *
   * <p>
   *
   * <pre>
   * import static tw.com.fcb.mimosa.http.APIErrorException.apiError;
   *
   * Optional optional = ...;
   * // ...
   * optional.orElseThrow(apiError(err -> err.code("ERR_E_XXX").message("something went wrong!")));
   * </pre>
   *
   * @param error
   * @see APIErrorT9nException#apiError(APIErrorT9n)
   * @return
   */
  public static Supplier<APIErrorT9nException> apiError(
      @NonNull UnaryOperator<APIErrorT9nBuilder> error) {
    return () -> new APIErrorT9nException(error);
  }

  /** @see #apiError(UnaryOperator) */
  public static Supplier<APIErrorT9nException> apiError(
      @NonNull UnaryOperator<APIErrorT9nBuilder> error, Throwable cause) {
    return () -> new APIErrorT9nException(error, cause);
  }

  final APIErrorT9n error;

  @Setter
  @Accessors(chain = true)
  Slf4jLevel loggingLevel;

  public APIErrorT9nException(@NonNull APIErrorT9n error) {
    this.error = error;
  }

  public APIErrorT9nException(@NonNull APIErrorT9n error, Throwable cause) {
    super(cause);
    this.error = error;
  }

  public APIErrorT9nException(@NonNull UnaryOperator<APIErrorT9nBuilder> error) {
    this(error.apply(APIErrorT9n.builder()).build());
  }

  public APIErrorT9nException(@NonNull UnaryOperator<APIErrorT9nBuilder> error, Throwable cause) {
    this(error.apply(APIErrorT9n.builder()).build(), cause);
  }
}
