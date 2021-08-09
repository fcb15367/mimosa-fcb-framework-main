package tw.com.fcb.mimosa.dddstructure.remittance.application.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static tw.com.fcb.mimosa.dddstructure.remittance.types.RemitCurrency.TWD;
import static tw.com.fcb.mimosa.dddstructure.remittance.types.RemitType.A;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tw.com.fcb.mimosa.dddstructure.remittance.application.assembler.RemittanceAssembler;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitBank;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitMoney;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.Remittance;

/** @author Matt Ho */
class RemittanceAssemblerTest {

  static RemittanceAssembler assembler;

  @BeforeAll
  static void setup() {
    assembler = Mappers.getMapper(RemittanceAssembler.class);
  }

  @Test
  void testToDto() {
    var expected = Remittance.builder()
        .money(RemitMoney.builder().amount(1000).currency(TWD).type(A).build())
        .bank(RemitBank.builder().code("007").transactionDate(LocalDate.now()).build())
        .build();
    expected.setId(1L);
    var actual = assembler.toDto(expected);
    assertThat(actual).isNotNull().extracting("id").isEqualTo(expected.getId());
    assertThat(actual)
        .extracting("remitAmount", "remitType", "remitCurrency")
        .containsExactly(
            expected.getMoney().getAmount(),
            expected.getMoney().getType(),
            expected.getMoney().getCurrency());
    assertThat(actual)
        .extracting("bankCode", "transactionDate")
        .containsExactly(expected.getBank().getCode(), expected.getBank().getTransactionDate());
  }
}
