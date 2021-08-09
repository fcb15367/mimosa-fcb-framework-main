package tw.com.fcb.mimosa.examples.openapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.com.fcb.mimosa.http.APIResponse;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author Jason Wu
 */
@Slf4j
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "使用者")
@Validated
@RequiredArgsConstructor
public class UserController {

  final UserService userService;

  @Operation(summary = "新增使用者")
  @PostMapping
  public APIResponse<Long> createUser(
      @Parameter(description = "必須傳入name及age", required = true) @Validated @RequestBody UserRequest user) {
    return APIResponse.success(userService.createUser(user));
  }

  @Operation(summary = "取得所有使用者清單")
  @GetMapping
  public APIResponse<Map<Long, UserRequest>> getAllUser() {
    return APIResponse.success(userService.getAllUser());
  }

  @Operation(summary = "取得特定id使用者")
  @GetMapping("/{id}")
  public APIResponse<UserRequest> getUser(@Parameter(description = "使用者id", required = true) @PathVariable("id") long id) {
    return APIResponse.success(userService.getUser(id));
  }

  @Operation(summary = "修改使用者")
  @PutMapping("/{id}")
  public APIResponse<Long> updateUser(@PathVariable("id") @NotNull long id, @RequestBody UserRequest user) {
    return APIResponse.success(userService.updateUser(id, user));
  }

  @Operation(summary = "刪除使用者")
  @DeleteMapping("/{id}")
  public APIResponse deleteUser(@PathVariable("id") @NotNull long id) {
    userService.deleteUser(id);
    return APIResponse.success();
  }

}
