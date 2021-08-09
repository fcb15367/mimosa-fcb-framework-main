import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tw.com.fcb.mimosa.ext.cache.support.MimosaCacheManager;

import java.util.Map;

/**
 * @author Jason Wu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  final UserRepository userRepository;
  static final String USER_CACHE = "mimosa:examples:users";
  final MimosaCacheManager cacheManager;

  public Long createUser(UserRequest user) {
    var id = (long) (Math.random() * 1000);
    userRepository.createUser(id, user);
    return id;
  }

  public Map<Long, UserRequest> getAllUser() {
    return userRepository.getAllUser();
  }

  public UserRequest getUser(long id) {
    return cacheManager.get(
        USER_CACHE,
        id,
        UserRequest.class,
        userRepository::getUser).orElse(null);
  }

  public Long updateUser(long id, UserRequest tempUser) {
    userRepository.updateUser(id, tempUser);
    cacheManager.evict(USER_CACHE, id);
    return id;
  }

  public void deleteUser(long id) {
    userRepository.deleteUser(id);
    cacheManager.evict(USER_CACHE, id);
  }
}
