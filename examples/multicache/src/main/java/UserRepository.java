import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import tw.com.fcb.mimosa.ext.cache.support.MimosaCacheManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Jason Wu
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepository {

  static Map<Long, UserRequest> database = new HashMap<>();
  final MimosaCacheManager cacheManager;

  public void createUser(long id, UserRequest user) {
    database.put(id, user);
    log.info("UserRepository 新增使用者 [id:{},{}]", id, user);
  }

  public Optional<UserRequest> getUser(long id) {
    log.info("UserRepository 取得id={}的使用者", id);
    return Optional.ofNullable(database.get(id));
  }

  public Map<Long, UserRequest> getAllUser() {
    log.info("UserRepository 取得所有使用者");
    return database;
  }

  public void deleteUser(long id) {
    log.info("UserRepository 刪除id={}的使用者", id);
    database.remove(id);
  }

  public void updateUser(long id, UserRequest tempUser) {
    UserRequest user = database.get(id);
    if (tempUser.getName() != null)
      user.setName(tempUser.getName());
    if (tempUser.getAge() != null && tempUser.getAge() > 0)
      user.setAge(tempUser.getAge());
    log.info("UserRepository 更新id={}的使用者", id);
  }
}
