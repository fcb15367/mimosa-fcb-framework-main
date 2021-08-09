package tw.com.fcb.mimosa.web;

import static lombok.AccessLevel.PACKAGE;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tw.com.fcb.mimosa.http.APIError;

/** @author Matt Ho */
@Schema(description = "錯誤結構 with traces")
@Data
@SuperBuilder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
class TracedAPIError extends APIError {

  @ArraySchema(schema = @Schema(description = "Root cause stacktrace", nullable = true))
  List<String> traces;
}
