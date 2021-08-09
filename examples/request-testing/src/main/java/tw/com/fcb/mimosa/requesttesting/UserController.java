package tw.com.fcb.mimosa.requesttesting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import tw.com.fcb.mimosa.http.APIRequest;
import tw.com.fcb.mimosa.http.APIResponse;

/**
 * @author Jason Wu
 */
@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

  @PostMapping
  public APIResponse<APIRequest<User>> requestTest1(@RequestBody APIRequest<User> request) {
    return APIResponse.success(request);
  }

}
