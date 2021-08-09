package tw.com.fcb.mimosa.dddstructure.sharedkernal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import tw.com.fcb.mimosa.ext.ddd.event.DomainEvent;

/** @author Matt Ho */
@Service
@RequiredArgsConstructor
public class EventStoreService {

  final ObjectMapper mapper;
  final EventStoreRepository repository;

  @SneakyThrows
  public void save(@NonNull DomainEvent event) {
    repository.save(
        Event.builder()
            .eventTime(event.getTime())
            .eventType(event.getType())
            .aggregateId(mapper.writeValueAsString(event.getAggregateId().get()))
            .event(mapper.writeValueAsString(event.getEvent()))
            .build());
  }
}
