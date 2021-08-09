package tw.com.fcb.mimosa.ext.ddd.event;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

/**
 * Abstract base class for domain events.
 *
 * @author Matt Ho
 */
@Getter
@AllArgsConstructor(access = PROTECTED)
public class DomainEvent {

  @NonNull
  final LocalDateTime time;

  // 為了適用於 Aggregate Create 的情境: aggregate id 是在 tx 結束時才會有, 所以這邊只能放 supplier
  @NonNull
  final Supplier<Object> aggregateId;

  @NonNull
  final Object event;

  protected DomainEvent(@NonNull Supplier<Object> aggregateId, @NonNull Object event) {
    this(LocalDateTime.now(), aggregateId, event);
  }

  protected DomainEvent(@NonNull Object aggregateId, @NonNull Object event) {
    this(() -> aggregateId, event);
  }

  public final String getType() {
    return this.getClass().getName();
  }
}
