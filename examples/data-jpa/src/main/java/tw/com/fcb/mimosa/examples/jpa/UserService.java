package tw.com.fcb.mimosa.examples.jpa;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.com.fcb.mimosa.http.APIErrorException;

/**
 * @author Jason Wu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  final UserRepository userRepository;
  final UserMapper mapper;

  public Long createUser(UserRequest user) {
    return userRepository.save(mapper.toEntity(user)).getId();
  }

  public Page<UserDto> getAllUser(Pageable pageable) {
    return userRepository.findAll(pageable).map(mapper::fromEntity);
  }

  public UserDto getUser(long id) {
    return mapper.fromEntity(userRepository.findById(id).get());
  }

  public UserDto updateUser(long id, ModifiedUser tempUser) {
    return userRepository
        .findById(id)
        .map(found -> mapper.copy(tempUser, found))
        .map(userRepository::save)
        .map(mapper::fromEntity)
        .orElseThrow(() -> new APIErrorException(
            err -> err.code("ID_NOT_EXIST").message(format("User.id %d not exist", id))));
  }

  public void deleteUser(long id) {
    userRepository.deleteById(id);
  }

  public Page<UserDto> findByCriteria(UserCriteria criteria, Pageable pageable) {
    return userRepository.findAll((root, query, builder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (hasText(criteria.getName())) {
        predicates.add(builder.equal(root.get("name"), criteria.getName()));
      }
      if (criteria.getAge() != null) {
        predicates.add(builder.equal(root.get("age"), criteria.getAge()));
      } else {
        if (criteria.getMaxAge() != null) {
          predicates.add(builder.le(root.get("age"), criteria.getMaxAge()));
        }
        if (criteria.getMinAge() != null) {
          predicates.add(builder.ge(root.get("age"), criteria.getMinAge()));
        }
      }
      return builder.and(predicates.toArray(Predicate[]::new));
    }, pageable).map(mapper::fromEntity);
  }
}
