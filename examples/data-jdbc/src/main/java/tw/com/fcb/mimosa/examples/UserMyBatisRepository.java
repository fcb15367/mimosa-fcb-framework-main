package tw.com.fcb.mimosa.examples;

import org.springframework.data.domain.Pageable;

import java.util.List;

/** @author Jason Wu */
public interface UserMyBatisRepository {

  List<UserEntity> findByCriteria(UserCriteria criteria, Pageable pageable);

  long countByCriteria(UserCriteria criteria);
}
