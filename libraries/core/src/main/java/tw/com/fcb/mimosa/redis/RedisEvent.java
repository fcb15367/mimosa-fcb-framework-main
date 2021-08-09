package tw.com.fcb.mimosa.redis;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** @param <T> Status */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "事件")
public class RedisEvent<T> {

  /** 發生時間 */
  @Default
  @Schema(description = "發生時間")
  LocalDateTime time = now();

  /** 狀態 */
  @NotNull
  @Schema(description = "狀態")
  T status;

  /** 訊息 */
  @NotBlank
  @Schema(description = "訊息")
  String message;
}
