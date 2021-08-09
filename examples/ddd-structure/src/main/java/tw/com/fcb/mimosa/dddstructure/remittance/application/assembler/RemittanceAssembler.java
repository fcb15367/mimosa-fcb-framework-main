package tw.com.fcb.mimosa.dddstructure.remittance.application.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.Remittance;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceDto;

/** @author Matt Ho */
@Mapper
public interface RemittanceAssembler {

  @Mapping(target = "remitAmount", source = "money.amount")
  @Mapping(target = "remitType", source = "money.type")
  @Mapping(target = "remitCurrency", source = "money.currency")
  @Mapping(target = "bankCode", source = "bank.code")
  @Mapping(target = "transactionDate", source = "bank.transactionDate")
  RemittanceDto toDto(Remittance remittance);
}
