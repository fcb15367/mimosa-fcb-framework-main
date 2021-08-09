package tw.com.fcb.mimosa.ext.data.jdbc.event;

import static org.springframework.core.ResolvableType.forClass;

import lombok.Getter;

/**
 * An callback that gets invoked after repository method proceeded.
 *
 * @param <R> Repository
 */
public abstract class AfterProceedCallback<R> {

  @Getter
  final Class<?> _type;

  protected AfterProceedCallback() {
    _type = forClass(getClass()).as(AfterProceedCallback.class).getGeneric(0).resolve();
  }

  public abstract void onAfterProceed(AfterProceedEvent event);
}
