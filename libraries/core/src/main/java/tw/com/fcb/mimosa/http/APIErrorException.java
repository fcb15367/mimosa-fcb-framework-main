package tw.com.fcb.mimosa.http;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import tw.com.fcb.mimosa.http.APIError.APIErrorBuilder;

/**
 * 透過 Exception 來控制 (中斷) 程式流程, 並且指定期望的 {@code APIError}
 *
 * @see APIError
 * @author Matt Ho
 */
@Getter
public class APIErrorException extends RuntimeException implements SpecifiedLoggingLevel {

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
   * @see APIErrorException#apiError(UnaryOperator)
   * @return
   */
  public static Supplier<APIErrorException> apiError(@NonNull APIError error) {
    return () -> new APIErrorException(error);
  }

  /** @see #apiError(APIError) */
  public static Supplier<APIErrorException> apiError(@NonNull APIError error, Throwable cause) {
    return () -> new APIErrorException(error, cause);
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
   * @see APIErrorException#apiError(APIError)
   * @return
   */
  public static Supplier<APIErrorException> apiError(@NonNull UnaryOperator<APIErrorBuilder> error) {
    return () -> new APIErrorException(error);
  }

  /** @see #apiError(UnaryOperator) */
  public static Supplier<APIErrorException> apiError(
      @NonNull UnaryOperator<APIErrorBuilder> error, Throwable cause) {
    return () -> new APIErrorException(error, cause);
  }

  final APIError error;

  @Setter
  @Accessors(chain = true)
  Slf4jLevel loggingLevel;

  public APIErrorException(@NonNull APIError error) {
    this.error = error;
  }

  public APIErrorException(@NonNull APIError error, Throwable cause) {
    super(cause);
    this.error = error;
  }

  public APIErrorException(@NonNull UnaryOperator<APIErrorBuilder> error) {
    this(error.apply(APIError.builder()).build());
  }

  public APIErrorException(@NonNull UnaryOperator<APIErrorBuilder> error, Throwable cause) {
    this(error.apply(APIError.builder()).build(), cause);
  }
}
