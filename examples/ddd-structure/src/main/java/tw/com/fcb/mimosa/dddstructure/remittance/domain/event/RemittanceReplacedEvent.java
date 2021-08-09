package tw.com.fcb.mimosa.dddstructure.remittance.domain.event;

import lombok.NonNull;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemitMoney;
import tw.com.fcb.mimosa.ext.ddd.event.DomainEvent;

/** @author Matt Ho */
public class RemittanceReplacedEvent extends DomainEvent {

  public RemittanceReplacedEvent(@NonNull Long id, @NonNull RemitMoney event) {
    super(id, event);
  }
}
