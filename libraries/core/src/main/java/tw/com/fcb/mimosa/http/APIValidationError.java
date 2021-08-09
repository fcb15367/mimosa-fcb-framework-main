package tw.com.fcb.mimosa.http;

import static lombok.AccessLevel.PACKAGE;
import static org.springframework.util.StringUtils.hasText;

import javax.validation.ConstraintViolation;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * 驗證錯誤明細
 *
 * @author Matt Ho
 */
@Schema(description = "驗證錯誤明細")
@Data
@SuperBuilder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
public class APIValidationError extends APIErrorDetail {

  /** 驗證對象 */
  @Schema(description = "驗證對象")
  String object;

  /** 驗證欄位 */
  @Schema(description = "驗證欄位")
  String field;

  /** 錯誤值 */
  @Schema(description = "錯誤值")
  Object rejectedValue;

  public APIValidationError(@NonNull ConstraintViolation<?> cv) {
    this.code = "constraint_violation";
    this.object = cv.getRootBeanClass().getSimpleName();
    this.field = cv.getPropertyPath().toString();
    this.rejectedValue = cv.getInvalidValue();
    this.message = cv.getMessage();
  }

  public APIValidationError(@NonNull ObjectError oe) {
    this.code = "object_error";
    this.object = oe.getObjectName();
    this.message = oe.getDefaultMessage();
    if (oe instanceof FieldError) {
      var fe = (FieldError) oe;
      this.code = "field_error";
      this.field = fe.getField();
      this.rejectedValue = fe.getRejectedValue();
    }
  }

  @Override
  public String shortSummary() {
    var sb = new StringBuilder(super.shortSummary());
    if (sb.length() > 0) {
      sb.append("/");
    }
    sb.append("error in object '" + object + "'");
    if (hasText(field)) {
      sb.append(" on field '" + field + "'");
    }
    if (rejectedValue != null) {
      sb.append("/rejected value [").append(rejectedValue).append("]");
    }
    return sb.toString();
  }
}
