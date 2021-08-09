package tw.com.fcb.mimosa.dddstructure.remittance.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitMoney;

/** @author Matt Ho */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReplaceRemittanceCommand {

  Long id;
  RemitMoney money;
}
