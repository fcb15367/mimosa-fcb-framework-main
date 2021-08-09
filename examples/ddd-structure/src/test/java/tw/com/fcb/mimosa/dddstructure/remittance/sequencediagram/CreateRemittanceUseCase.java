package tw.com.fcb.mimosa.dddstructure.remittance.sequencediagram;

/** @author Matt Ho */
class CreateRemittanceUseCase {

  RemittanceRepository repository;

  void execute(CreateRemittanceCommand command) {
    var remittance = Remittance.create(command);
    repository.save(remittance);
  }
}
