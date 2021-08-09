package tw.com.fcb.mimosa.http;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static lombok.AccessLevel.PACKAGE;

import java.sql.SQLException;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/** @author Matt Ho */
@Schema(description = "SQL 錯誤明細")
@Data
@SuperBuilder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
public class APISQLError extends APIErrorDetail {

  /** XOPEN or SQL:2003 code identifying the SQL exception */
  @Schema(description = "XOPEN or SQL:2003 code identifying the SQL exception")
  String sqlState;

  /** Database vendor-specific SQL exception code */
  @Schema(description = "Database vendor-specific SQL exception code")
  int vendorCode;

  public <E extends SQLException> APISQLError(@NonNull E ex) {
    super(code(ex), ex.getMessage());
    this.sqlState = ex.getSQLState();
    this.vendorCode = ex.getErrorCode();
  }

  static <E extends SQLException> String code(@NonNull E ex) {
    var name = ex.getClass().getSimpleName().replaceAll("Exception$", "");
    return UPPER_CAMEL.to(LOWER_UNDERSCORE, name);
  }
}
