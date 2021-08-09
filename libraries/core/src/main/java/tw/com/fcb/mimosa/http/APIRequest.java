package tw.com.fcb.mimosa.http;

import static lombok.AccessLevel.PACKAGE;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

@Schema(description = "API 請求結構")
@Data
@Builder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
@Accessors(chain = true)
public class APIRequest<T> {

  /** 自定義 request header */
  @Schema(description = "自定義request header", nullable = true)
  Map<String, String> header;

  /** 自定義 body */
  @Schema(description = "自定義body", nullable = true)
  T body;
}
