package tw.com.fcb.mimosa.ext.data.jdbc;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import lombok.NonNull;

/** @author Matt Ho */
class SqlToUseHolder {

  private static final ThreadLocal<SqlToUse> contextHolder = new ThreadLocal<>();

  /** Clears the current context. */
  static void clearContext() {
    contextHolder.remove();
  }

  /**
   * Obtains the current context.
   *
   * @return a context (never <code>null</code> - create a default implementation if necessary)
   */
  static Optional<SqlToUse> getContext() {
    return ofNullable(contextHolder.get());
  }

  /**
   * Sets the current context.
   *
   * @param context to the new argument (should never be <code>null</code>, although implementations
   *        must check if <code>null</code> has been passed and throw an <code>IllegalArgumentException
   *     </code> in such cases)
   */
  static void setContext(@NonNull SqlToUse context) {
    contextHolder.set(context);
  }
}
