package tw.com.fcb.mimosa.dddstructure.remittance.application.command;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitBank;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitMoney;

/** @author Matt Ho */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateRemittanceCommand {

  @NotNull
  RemitMoney money;
  @NotNull
  RemitBank bank;
}
