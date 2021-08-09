package tw.com.fcb.mimosa.dddstructure.remittance.domain.event;

import lombok.NonNull;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.Remittance;
import tw.com.fcb.mimosa.ext.ddd.event.DomainEvent;

/** @author Matt Ho */
public class RemittanceCreatedEvent extends DomainEvent {

  public RemittanceCreatedEvent(@NonNull Remittance event) {
    super(event::getId, event);
  }
}
