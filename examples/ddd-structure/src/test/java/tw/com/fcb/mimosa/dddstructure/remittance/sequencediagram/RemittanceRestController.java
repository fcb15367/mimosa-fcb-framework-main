package tw.com.fcb.mimosa.dddstructure.remittance.sequencediagram;

/** @author Matt Ho */
class RemittanceRestController {

  QueryRemittance query;
  CreateRemittanceUseCase useCase;

  void findAll() {
    query.findAll();
  }

  void createRemittance() {
    useCase.execute(new CreateRemittanceCommand());
  }
}
