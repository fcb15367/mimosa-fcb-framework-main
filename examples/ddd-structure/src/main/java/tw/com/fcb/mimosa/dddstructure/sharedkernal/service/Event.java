package tw.com.fcb.mimosa.dddstructure.sharedkernal.service;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/** @author Matt Ho */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("EVENT")
public class Event {

  @Id
  Long id;

  @Column("EVENT_TIME")
  LocalDateTime eventTime;

  @Column("EVENT_TYPE")
  String eventType;

  @Column("AGGREGATE_ID")
  String aggregateId;

  @Column("EVENT")
  String event;
}
