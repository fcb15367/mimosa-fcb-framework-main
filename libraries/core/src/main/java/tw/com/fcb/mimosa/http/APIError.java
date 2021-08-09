package tw.com.fcb.mimosa.http;

import static java.util.Collections.emptyList;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.util.StringUtils.hasText;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import tw.com.fcb.mimosa.domain.t9n.Translated;

/**
 * 錯誤結構
 *
 * @author Matt Ho
 */
@Schema(description = "錯誤結構")
@Data
@SuperBuilder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
public class APIError {

  /** 錯誤代碼 */
  @Schema(description = "錯誤代碼")
  @NonNull
  String code;

  /** 錯誤訊息 */
  @Schema(description = "錯誤訊息")
  String message;

  /** 錯誤明細 */
  @ArraySchema
  @Singular
  List<? extends APIErrorDetail> details;

  public APIError(@NonNull Translated translated) {
    this(translated, emptyList());
  }

  public APIError(@NonNull Translated translated, @NonNull List<? extends APIErrorDetail> details) {
    this.code = translated.getCode();
    this.message = translated.getTranslation();
    this.details = details;
  }

  public String shortSummary() {
    var sb = new StringBuilder(code);
    if (hasText(message)) {
      sb.append(": ").append(message);
    }
    if (!details.isEmpty()) {
      var delimiter = ", ";
      sb.append(": [");
      details.forEach(detail -> sb.append(detail.shortSummary()).append(delimiter));
      sb.replace(sb.length() - delimiter.length(), sb.length(), "]");
    }
    return sb.toString();
  }
}
