package tw.com.fcb.mimosa.ext.ddd.application;

import lombok.NonNull;

/**
 * Use-cases dispatcher (調度器)
 *
 * <p>
 * 調度器有唯一規則: <b>{@code Command} 及 {@code ApplicationUseCase} 的關聯必須是 1:1</b>
 *
 * @see SpringBeanUseCases
 * @see ApplicationUseCase
 * @author Matt Ho
 */
public interface UseCases {

  /**
   * Execute use-case for {@code command}
   *
   * @throws NoSuchUseCaseDefinitionException if there is no such use-case definition for the
   *         command
   * @see ApplicationUseCase
   * @return
   */
  <Command, DTO> DTO execute(@NonNull Command command) throws NoSuchUseCaseDefinitionException;
}
