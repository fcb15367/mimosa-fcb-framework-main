package tw.com.fcb.mimosa.dddstructure.remittance.infra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.Remittance;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemittanceRepository;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceCriteria;

/** @author Matt Ho */
interface RemittanceRepositoryImpl
    extends RemittanceRepository, PagingAndSortingRepository<Remittance, Long> {

  @Override
  default Page<Remittance> findAll(RemittanceCriteria criteria, Pageable pageable) {
    return findAll(pageable); // 這邊不處理過濾條件, 動態的條件過濾請參考 examples/data-jdbc 範例
  }
}
