package tw.com.fcb.mimosa.examples.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.data.Envelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tw.com.fcb.mimosa.examples.entity.KafkaMessage;
import tw.com.fcb.mimosa.examples.entity.KafkaMessagePayload;
import tw.com.fcb.mimosa.examples.entity.TargetCustomerEntity;
import tw.com.fcb.mimosa.examples.service.CustomerService;
import java.io.IOException;

/**
 * @author Jason Wu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

  private final CustomerService customerService;
  final ObjectMapper objectMapper;

  @KafkaListener(topics = { "example_cdc.customerdb.source_customer" })
  public void receive(String message) throws IOException {
    log.debug("消費消息: {}", message);
    KafkaMessage kafkaMessage = objectMapper.readValue(message, KafkaMessage.class);
    KafkaMessagePayload sourceRecordChangeValue = kafkaMessage.getPayload();
    log.debug("op: {}", sourceRecordChangeValue.getOp());
    log.debug("Payload: {}", sourceRecordChangeValue);

    if (sourceRecordChangeValue != null) {
      Envelope.Operation operation = Envelope.Operation.forCode(sourceRecordChangeValue.getOp());

      if (operation != Envelope.Operation.READ) {
        TargetCustomerEntity targetCustomerEntity = operation == Envelope.Operation.DELETE ? sourceRecordChangeValue.getBefore()
            : sourceRecordChangeValue.getAfter();

        this.customerService.replicateData(targetCustomerEntity, operation);
        log.info("Updated Data: {} with Operation: {}", objectMapper.writeValueAsString(sourceRecordChangeValue.getAfter()),
            operation.name());
      }
    }

  }
}
