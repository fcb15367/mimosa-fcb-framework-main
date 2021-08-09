package tw.com.fcb.mimosa.ext.ddd.application;

import static org.springframework.core.ResolvableType.forClass;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * {@code ApplicationUseCase} 代表了一個接受 {@code Command}, 並處理成 {@code DTO} 的程式.
 *
 * <p>
 * 想要使用 {@code ApplicationUseCase} 的程式可以:
 *
 * <ul>
 * <li>直接 Inject {@code ApplicationUseCase} Bean 使用之
 * <li>Inject {@code UseCases} Bean, 透過執行 {@link UseCases#execute(Object)}, 自動的找到傳入 {@code
 *       Command} 所關聯的 {@code ApplicationUseCase} 去執行
 * </ul>
 *
 * @see UseCases
 * @author Matt Ho
 */
public interface ApplicationUseCase<Command, DTO> {

  /**
   * 執行使用案例
   *
   * @param command 使用案例所需的命令
   * @return 執行結果
   */
  DTO execute(@NotNull @Valid Command command);

  default Class<?> getCommandType() {
    return forClass(getClass()).as(ApplicationUseCase.class).getGeneric(0).resolve();
  }
}
