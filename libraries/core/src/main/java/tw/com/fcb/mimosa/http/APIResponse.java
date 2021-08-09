package tw.com.fcb.mimosa.http;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PACKAGE;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import tw.com.fcb.mimosa.http.APIError.APIErrorBuilder;

/**
 * API 回應結構
 *
 * @author Matt Ho
 * @param <T>
 */
@Schema(description = "API 回應結構")
@Data
@Builder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
@Accessors(chain = true)
public class APIResponse<T> {

  /** 是否成功 */
  @Schema(description = "是否成功")
  boolean success;
  /** 錯誤 */
  @Schema(description = "錯誤訊息", nullable = true)
  APIError error;
  /** 成功資料 */
  @Schema(description = "成功資料", nullable = true)
  T clientResponse;
  /** 來源Id */
  @Schema(description = "來源Id", nullable = true)
  String sourceId;
  /** 回應Id */
  @Schema(description = "回應Id", nullable = true)
  String responseId;
  /** transactionId */
  @Schema(description = "transactionId", nullable = true)
  String transactionId;

  /** 狀態代碼 */
  @Schema(description = "狀態代碼", nullable = true)
  String statusCode;

  /** 狀態訊息 */
  @Schema(description = "狀態訊息", nullable = true)
  String statusMsg;

  /**
   * 建構成功 {@code APIResponse}
   *
   * @param <T>
   * @return
   */
  public static <T> APIResponse<T> success() {
    return success(null);
  }

  /**
   * 建構成功 {@code APIResponse}, 且帶著的資料
   *
   * @param data
   * @param <T>
   * @return
   */
  public static <T> APIResponse<T> success(@Nullable T data) {
    return success(data, null, null);
  }

  /**
   * 建構成功 {@code APIResponse}, 且帶著的資料、狀態代碼
   *
   * @param data
   * @param statusCode
   * @param <T>
   * @return
   */
  public static <T> APIResponse<T> success(@Nullable T data, @Nullable String statusCode) {
    return success(data, statusCode, null);
  }

  /**
   * 建構成功 {@code APIResponse}, 且帶著的資料、狀態代碼、狀態訊息
   *
   * @param data
   * @param statusCode
   * @param statusMsg
   * @param <T>
   * @return
   */
  public static <T> APIResponse<T> success(@Nullable T data, @Nullable String statusCode, @Nullable String statusMsg) {
    return new APIResponse<>(true, null, data, null, null, null, statusCode, statusMsg);
  }

  /**
   * 建構失敗 {@code APIResponse}, 且透過 {@code APIErrorBuilder} 指定錯誤訊息內容
   *
   * @param error
   * @see APIErrorBuilder
   * @param <T>
   * @return
   */
  public static <T> APIResponse<T> error(@NonNull UnaryOperator<APIErrorBuilder> error) {
    return error(error.apply(APIError.builder()).build());
  }

  /**
   * 建構失敗 {@code APIResponse}, 且帶著錯誤訊息
   *
   * @param error
   * @param <T>
   * @return
   */
  public static <T> APIResponse<T> error(@NonNull APIError error) {
    return new APIResponse<>(false, error, null, null, null, null, error.getCode(), error.getMessage());
  }

  /**
   * 必須成功, 返回資料內容; 否則丟出例外
   *
   * @see #toOptional()
   * @return data
   * @throws UnsatisfiedResponseException if it's not success
   */
  public T mustSuccess() throws UnsatisfiedResponseException {
    if (!success) {
      throw new UnsatisfiedResponseException(this);
    }
    return clientResponse;
  }

  /**
   * 當不需要處理失敗, 在意的是成功時的 data, 這個方法可以將 data 串接到 {@code Optional} api
   *
   * @see #mustSuccess()
   * @return Optional.empty() is not success or data is null
   */
  public Optional<T> toOptional() {
    return ofNullable(clientResponse).filter(data -> success);
  }

  /**
   * 指定當成功時要處理的內容
   *
   * @param dataConsumer
   * @return
   */
  public APIResponse<T> ifSuccess(@NonNull Consumer<T> dataConsumer) {
    if (success) {
      dataConsumer.accept(clientResponse);
    }
    return this;
  }

  /**
   * 指定當失敗時要處理的內容
   *
   * @param errorConsumer
   * @return
   */
  public APIResponse<T> ifError(@NonNull Consumer<APIError> errorConsumer) {
    if (!success) {
      errorConsumer.accept(error);
    }
    return this;
  }

  /**
   * 將 data 轉換成另一種型態
   *
   * @param converter
   * @param <U>
   * @return new instance of {@code APIResponse}
   */
  public <U> APIResponse<U> map(@NonNull Function<? super T, ? extends U> converter) {
    return APIResponse.<U> builder()
        .clientResponse(ofNullable(clientResponse).map(converter).orElse(null))
        .error(error)
        .success(success)
        .build();
  }
}
