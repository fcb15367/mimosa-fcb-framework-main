package tw.com.fcb.mimosa.ext.ddd.application;

import java.util.Map;

import org.springframework.beans.factory.ListableBeanFactory;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.EntryStream;

/**
 * 在 App 啟動階段, 會自動的找到所有實作了 {@code ApplicationUseCase} 的 Spring Bean, 並將該 Bean 及在 {@code
 * ApplicationUseCase} Generic 中宣告的 {@code Command} 做關聯
 *
 * <p>
 * 於 Runtime 時, 會自動的找到傳入 {@code Command} 所關聯的 {@code ApplicationUseCase} 去執行
 *
 * <p>
 * 需注意的是, {@code Command} 及 {@code ApplicationUseCase} 的關聯必須是 1:1, 若有 {@code Command} 關聯到多個
 * {@code ApplicationUseCase}, 在啟動時會丟出 {@code NoUniqueUseCaseDefinitionException}
 *
 * @see ApplicationUseCase
 * @see UseCases
 * @see NoUniqueUseCaseDefinitionException
 * @author Matt Ho
 */
@Slf4j
class SpringBeanUseCases implements UseCases {

  /**
   * Use Cases 集合
   *
   * <p>
   * Key: Command type, Value: ApplicationUseCase bean name
   *
   * <p>
   * 這邊不直接 cache bean reference 是因為不想破壞專案 singleton bean 或 prototype bean 的設定, 因此這邊才只記住 bean
   * name, runtime 才會向 spring 取 bean reference 來使用
   */
  final Map<Class, String> useCases;

  final ListableBeanFactory beanFactory;

  SpringBeanUseCases(ListableBeanFactory beanFactory) {
    this.beanFactory = beanFactory;
    this.useCases = EntryStream.of(this.beanFactory.getBeansOfType(ApplicationUseCase.class))
        .mapValues(ApplicationUseCase::getCommandType)
        .invert()
        .peekKeyValue(
            (command, useCase) -> log.debug(
                "Identified command type '{}' for use-case bean '{}'",
                command.getName(),
                useCase))
        .toMap(
            (useCase1, useCase2) -> {
              throw new NoUniqueUseCaseDefinitionException(useCase1, useCase2);
            });
  }

  @Override
  public <Command, DTO> DTO execute(@NonNull Command command)
      throws NoSuchUseCaseDefinitionException {
    log.debug("Looking up use-case for command type: {}", command.getClass().getName());
    var useCaseName = this.useCases.get(command.getClass());
    if (useCaseName == null) {
      throw new NoSuchUseCaseDefinitionException(command);
    }
    log.debug("Executing use-case bean '{}' for command: {}", useCaseName, command);
    return (DTO) this.beanFactory.getBean(useCaseName, ApplicationUseCase.class).execute(command);
  }
}
