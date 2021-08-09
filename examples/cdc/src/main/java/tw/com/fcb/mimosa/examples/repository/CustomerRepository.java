package tw.com.fcb.mimosa.examples.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.stereotype.Repository;
import tw.com.fcb.mimosa.examples.entity.TargetCustomerEntity;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<TargetCustomerEntity, Long> {
}
