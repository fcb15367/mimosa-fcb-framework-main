package tw.com.fcb.mimosa.examples.openapi;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

  static Map<Long, UserRequest> database = new HashMap<>();

  public Long createUser(UserRequest user) {
    var id = (long) (Math.random() * 1000);
    database.put(id, user);
    return id;
  }

  public Map<Long, UserRequest> getAllUser() {
    return database;
  }

  public UserRequest getUser(long id) {
    return database.get(id);
  }

  public Long updateUser(long id, UserRequest tempUser) {
    UserRequest user = database.get(id);
    if (tempUser.getName() != null)
      user.setName(tempUser.getName());
    if (tempUser.getAge() != null && tempUser.getAge() > 0)
      user.setAge(tempUser.getAge());
    return id;
  }

  public void deleteUser(long id) {
    database.remove(id);
  }
}
