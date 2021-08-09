package tw.com.fcb.mimosa.examples.jpa;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.fcb.mimosa.http.APIResponse;

/** @author Jason Wu */
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
  public APIResponse<Page<UserDto>> getAllUser(Pageable pageable) {
    return APIResponse.success(userService.getAllUser(pageable));
  }

  @Operation(summary = "取得特定id使用者")
  @GetMapping("/{id}")
  public APIResponse<UserDto> getUser(@Parameter(description = "使用者id", required = true) @PathVariable("id") long id) {
    return APIResponse.success(userService.getUser(id));
  }

  @Operation(summary = "修改使用者")
  @PutMapping("/{id}")
  public APIResponse<UserDto> updateUser(@PathVariable("id") @NotNull long id, @RequestBody ModifiedUser user) {
    return APIResponse.success(userService.updateUser(id, user));
  }

  @Operation(summary = "刪除使用者")
  @DeleteMapping("/{id}")
  public APIResponse deleteUser(@PathVariable("id") @NotNull long id) {
    userService.deleteUser(id);
    return APIResponse.success();
  }

  @Operation(summary = "給定條件查詢使用者")
  @GetMapping("/findbycriteria")
  public APIResponse<Page<UserDto>> findByCriteria(@NotNull UserCriteria criteria, @PageableDefault Pageable pageable) {
    return APIResponse.success(userService.findByCriteria(criteria, pageable));
  }
}
