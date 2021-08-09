package tw.com.fcb.mimosa.examples;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/** @author Jason Wu */
public interface UserRepository
    extends PagingAndSortingRepository<UserEntity, Long>, UserMyBatisRepository {
  Optional<UserEntity> findById(Long id);
}
