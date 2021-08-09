package tw.com.fcb.mimosa.dddstructure.remittance.application.usecase;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tw.com.fcb.mimosa.dddstructure.remittance.application.command.ReplaceRemittanceCommand;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.Remittance;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemittanceRepository;
import tw.com.fcb.mimosa.ext.ddd.application.ApplicationUseCase;

/** @author Matt Ho */
@Service
@Validated
@RequiredArgsConstructor
public class ReplaceRemittanceUseCase
    implements ApplicationUseCase<ReplaceRemittanceCommand, Long> {

  final RemittanceRepository repository;

  @Override
  public Long execute(ReplaceRemittanceCommand command) {
    return repository
        .findById(command.getId())
        .map(remittance -> remittance.replace(command.getMoney()))
        .map(repository::save)
        .map(Remittance::getId)
        .orElseThrow(
            () -> new IllegalArgumentException(
                format("Remittance.id [%s] not found", command.getId())));
  }
}
