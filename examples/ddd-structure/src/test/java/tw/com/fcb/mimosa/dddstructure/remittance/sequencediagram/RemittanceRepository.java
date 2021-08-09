package tw.com.fcb.mimosa.dddstructure.remittance.sequencediagram;

/** @author Matt Ho */
interface RemittanceRepository {

  void findAll();

  void save(Remittance remittance);
}
