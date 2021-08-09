package tw.com.fcb.mimosa.ext.ddd.application;

import static java.lang.String.format;

import lombok.NonNull;

/**
 * 這個例外會發生在執行 {@link UseCases#execute(Object)} 時, 傳入一個沒有任何對應 {@code ApplicationUseCase} 的 {@code
 * Command}
 *
 * @see UseCases
 * @author Matt Ho
 */
public class NoSuchUseCaseDefinitionException extends RuntimeException {

  NoSuchUseCaseDefinitionException(@NonNull Object command) {
    super(format("No use-case identified for command type: %s", command.getClass().getName()));
  }
}
