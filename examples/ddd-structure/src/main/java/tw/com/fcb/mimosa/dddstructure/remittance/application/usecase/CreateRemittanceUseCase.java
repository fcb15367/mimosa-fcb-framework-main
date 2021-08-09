package tw.com.fcb.mimosa.dddstructure.remittance.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import tw.com.fcb.mimosa.dddstructure.remittance.application.command.CreateRemittanceCommand;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.Remittance;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemittanceRepository;
import tw.com.fcb.mimosa.ext.ddd.application.ApplicationUseCase;

/** @author Matt Ho */
@Service
@Validated
@RequiredArgsConstructor
public class CreateRemittanceUseCase
    implements ApplicationUseCase<CreateRemittanceCommand, Long> {

  final RemittanceRepository repository;

  @Override
  public Long execute(CreateRemittanceCommand command) {
    return repository.save(Remittance.create(command.getMoney(), command.getBank())).getId();
  }
}
