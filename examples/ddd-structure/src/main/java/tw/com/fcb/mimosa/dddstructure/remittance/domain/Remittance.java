package tw.com.fcb.mimosa.dddstructure.remittance.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.event.RemittanceCreatedEvent;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.event.RemittanceReplacedEvent;
import tw.com.fcb.mimosa.ext.data.jdbc.AuditMetadata;

/**
 * 匯款申請
 *
 * @author Matt Ho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table("REMITTANCE")
public class Remittance extends AbstractAggregateRoot<Remittance> {

  @Id
  @Column("ID")
  Long id;

  @Version
  @Column("VERSION")
  Long version;

  @Default
  @Embedded.Empty
  AuditMetadata<String> audit = new AuditMetadata<>();

  @Embedded.Empty
  RemitMoney money;

  @Embedded.Empty
  RemitBank bank;

  public static Remittance create(@NonNull RemitMoney money, @NonNull RemitBank bank) {
    var remittance = Remittance.builder().money(money).bank(bank).build();
    return remittance.andEvent(new RemittanceCreatedEvent(remittance));
  }

  public Remittance replace(@NonNull RemitMoney money) {
    this.money = money;
    return andEvent(new RemittanceReplacedEvent(id, money));
  }
}
