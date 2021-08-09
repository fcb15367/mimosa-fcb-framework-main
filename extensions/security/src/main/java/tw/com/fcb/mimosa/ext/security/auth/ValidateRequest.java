package tw.com.fcb.mimosa.ext.security.auth;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** 規格來自: Token驗證規格 (002).doc */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateRequest {
  @NotBlank
  private String token;
}
