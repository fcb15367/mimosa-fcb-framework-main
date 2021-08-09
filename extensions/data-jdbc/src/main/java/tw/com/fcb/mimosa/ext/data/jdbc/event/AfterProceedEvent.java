package tw.com.fcb.mimosa.ext.data.jdbc.event;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.lang.Nullable;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import tw.com.fcb.mimosa.ext.data.jdbc.MethodInvoked;
import tw.com.fcb.mimosa.ext.data.jdbc.SqlToUse;

/**
 * Gets published after repository method proceeded.
 *
 * @author Matt Ho
 * @param <R> Repository
 */
@Getter
@ToString
public class AfterProceedEvent<R> extends ApplicationEvent implements ResolvableTypeProvider {

  final MethodInvoked invoked;
  final Optional<SqlToUse> sqlToUse;
  final transient Optional<Object> proceed;
  final Optional<Throwable> throwing;

  @Builder
  AfterProceedEvent(
      @NonNull Class<R> repositoryClass,
      @NonNull MethodInvoked invoked,
      @NonNull Optional<SqlToUse> sqlToUse,
      @Nullable Object proceed,
      @Nullable Throwable throwing) {
    super(repositoryClass);
    this.invoked = invoked;
    this.sqlToUse = sqlToUse;
    this.proceed = ofNullable(proceed);
    this.throwing = ofNullable(throwing);
  }

  @Override
  public ResolvableType getResolvableType() {
    return ResolvableType.forClassWithGenerics(
        getClass(), ResolvableType.forClass((Class<R>) getSource()));
  }
}
