package tw.com.fcb.mimosa.dddstructure.remittance.rest;

import javax.validation.constraints.NotNull;
import lombok.Data;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitBank;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitMoney;

/** @author Matt Ho */
@Data
public class CreateRemittanceRequest {

  @NotNull
  RemitMoney money;

  @NotNull
  RemitBank bank;
}
