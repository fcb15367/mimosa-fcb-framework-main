package tw.com.fcb.mimosa.dddstructure.remittance.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tw.com.fcb.mimosa.ext.ddd.event.DomainEvent;
import tw.com.fcb.mimosa.dddstructure.sharedkernal.service.EventStoreService;

/** @author Matt Ho */
@Component
@RequiredArgsConstructor
class RemittanceEventListener {

  final EventStoreService service;

  @TransactionalEventListener
  void handleEvent(DomainEvent event) {
    service.save(event);
  }
}
