package tw.com.fcb.mimosa.dddstructure.remittance.sequencediagram;

/** @author Matt Ho */
class QueryRemittance {

  RemittanceRepository repository;

  void findAll() {
    repository.findAll();
  }
}
