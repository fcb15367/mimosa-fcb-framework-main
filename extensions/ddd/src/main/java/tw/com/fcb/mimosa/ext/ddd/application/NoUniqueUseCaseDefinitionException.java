package tw.com.fcb.mimosa.ext.ddd.application;

import static java.lang.String.format;

import lombok.NonNull;

/**
 * 這個例外會發生在 {@code SpringBeanUseCases} 在收集 {@code Command} 關聯的 {@code ApplicationUseCase} 時, 發現有
 * {@code Command} 跟多個 {@code ApplicationUseCase} 有所關聯.
 *
 * <p>
 * <b>{@code Command} 跟 {@code ApplicationUseCase} 必須是 1:1</b>
 *
 * @see ApplicationUseCase
 * @see SpringBeanUseCases
 * @author Matt Ho
 */
public class NoUniqueUseCaseDefinitionException extends RuntimeException {

  NoUniqueUseCaseDefinitionException(@NonNull String useCaseName1, @NonNull String useCaseName2) {
    super(format("Duplicate command type for use-case '%s' and '%s'", useCaseName1, useCaseName2));
  }
}
