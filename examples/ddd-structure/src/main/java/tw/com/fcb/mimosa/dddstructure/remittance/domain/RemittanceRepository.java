package tw.com.fcb.mimosa.dddstructure.remittance.domain;

import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceCriteria;

/** @author Matt Ho */
public interface RemittanceRepository {

  @Transactional(rollbackFor = Exception.class)
  Remittance save(Remittance remittance);

  @Transactional(readOnly = true)
  Optional<Remittance> findById(Long id);

  @Transactional(readOnly = true)
  Page<Remittance> findAll(RemittanceCriteria criteria, Pageable pageable);
}
