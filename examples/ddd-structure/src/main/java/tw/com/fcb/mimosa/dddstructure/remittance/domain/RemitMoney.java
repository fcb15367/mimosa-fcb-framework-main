package tw.com.fcb.mimosa.dddstructure.remittance.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemitCurrency;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemitType;

/**
 * 匯款金額
 *
 * @author Matt Ho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class RemitMoney {

  @Column("REMIT_AMOUNT")
  @Positive
  int amount;

  @Column("REMIT_TYPE")
  @NotNull
  RemitType type;

  @Column("REMIT_CURRENCY")
  @NotNull
  RemitCurrency currency;
}
