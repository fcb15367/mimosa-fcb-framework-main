package tw.com.fcb.mimosa.dddstructure.remittance.types;

import java.time.LocalDate;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @author Matt Ho */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemittanceDto {

  Long id;

  @Positive
  int remitAmount;

  @NotNull
  RemitType remitType;

  @NotNull
  RemitCurrency remitCurrency;

  @NotBlank
  String bankCode; // 受款行

  @NotNull
  @FutureOrPresent
  LocalDate transactionDate; // 交易日期
}
