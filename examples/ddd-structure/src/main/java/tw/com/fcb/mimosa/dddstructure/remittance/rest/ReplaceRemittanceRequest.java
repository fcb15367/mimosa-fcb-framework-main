package tw.com.fcb.mimosa.dddstructure.remittance.rest;

import javax.validation.constraints.NotNull;
import lombok.Data;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitMoney;

/** @author Matt Ho */
@Data
public class ReplaceRemittanceRequest {

  @NotNull
  RemitMoney money;

  // 假設 bank 不可修改
  //  @NotNull
  //  RemitBank bank;
}
