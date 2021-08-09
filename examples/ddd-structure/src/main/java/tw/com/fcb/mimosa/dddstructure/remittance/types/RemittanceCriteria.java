package tw.com.fcb.mimosa.dddstructure.remittance.types;

import lombok.Data;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemitCurrency;

/**
 * @author Matt Ho
 */
@Data
public class RemittanceCriteria {

  RemitCurrency currency;
  String bankCode;
}
