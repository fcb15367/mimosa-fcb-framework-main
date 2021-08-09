package tw.com.fcb.mimosa.dddstructure.remittance.domain;

import java.time.LocalDate;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;

/** @author Matt Ho */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RemitBank {

  @Column("BANK_CODE")
  @NotBlank
  String code; // 受款行

  @Column("TRANSACTION_DATE")
  @NotNull
  @FutureOrPresent
  LocalDate transactionDate; // 交易日期
}
