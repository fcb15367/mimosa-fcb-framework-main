package tw.com.fcb.mimosa.examples.service;

import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.examples.entity.CustomerMapper;
import tw.com.fcb.mimosa.examples.entity.TargetCustomerEntity;
import tw.com.fcb.mimosa.examples.repository.CustomerRepository;
import io.debezium.data.Envelope.Operation;
import org.springframework.stereotype.Service;
import tw.com.fcb.mimosa.http.APIErrorException;
import static java.lang.String.format;

/**
 * @author Jason Wu
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper mapper;

  public void replicateData(TargetCustomerEntity targetCustomerEntity, Operation operation) {

    if (Operation.DELETE.name().equals(operation.name())) {
      customerRepository.deleteById(targetCustomerEntity.getId());
    } else if (Operation.UPDATE.name().equals(operation.name())) {
      customerRepository
          .findById(targetCustomerEntity.getId())
          .map(found -> mapper.copy(mapper.toModifiedTargetCustomerEntity(targetCustomerEntity), found))
          .map(customerRepository::save)
          .orElseThrow(() -> new APIErrorException(
              err -> err.code("ID_NOT_EXIST").message(format("User.id %d not exist", targetCustomerEntity.getId()))));
    } else {
      targetCustomerEntity.setId(null);
      customerRepository.save(targetCustomerEntity);
    }
  }
}
