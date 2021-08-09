package tw.com.fcb.mimosa.http;

import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.util.StringUtils.hasText;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 錯誤明細
 *
 * @author Matt Ho
 */
@Schema(description = "錯誤明細")
@Data
@SuperBuilder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
public class APIErrorDetail {

  /** 錯誤代碼 */
  @Schema(description = "錯誤代碼", nullable = true)
  String code;

  /** 錯誤訊息 */
  @Schema(description = "錯誤訊息")
  String message;

  public String shortSummary() {
    var sb = new StringBuilder();
    if (hasText(code)) {
      sb.append(code);
    }
    if (hasText(message)) {
      if (sb.length() > 0) {
        sb.append("/");
      }
      sb.append(message);
    }
    return sb.toString();
  }
}
