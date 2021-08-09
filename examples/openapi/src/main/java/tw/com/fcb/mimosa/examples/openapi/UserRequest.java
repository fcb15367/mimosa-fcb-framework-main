package tw.com.fcb.mimosa.examples.openapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserRequest {

  @NotNull(message = "名字不得為空")
  String name;

  @NotNull(message = "年齡不得為空")
  @Min(value = 1, message = "id必須為正整數")
  Integer age;
}
