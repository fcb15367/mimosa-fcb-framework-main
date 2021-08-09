package tw.com.fcb.mimosa.ext.security;

import static tw.com.fcb.mimosa.ext.security.auth.ValidateResponse.SUCCESS_RTN_CODE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 無任何額外邏輯的 {@link UserDetails} 實作, 可以當成跟 {@link User} 一樣, 但額外加上了:
 *
 * <ol>
 * <li>客戶特有的欄位
 * <li>No-arguments constructor, 這樣才能做 JSON 的轉換
 * </ol>
 *
 * @author Matt Ho
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class SimpleUser implements UserDetails {

  static final SimpleUser ANONYMOUS = SimpleUser.builder()
      .username("anonymous")
      .password("anonymous")
      .custId("anonymous")
      .rtnCode(SUCCESS_RTN_CODE)
      .build();

  private String password;
  private String username;
  @Default
  private Set<GrantedAuthority> authorities = new HashSet<>();
  @Default
  private boolean accountNonExpired = true;
  @Default
  private boolean accountNonLocked = true;
  @Default
  private boolean credentialsNonExpired = true;
  @Default
  private boolean enabled = true;

  /**
   * 身分證字號
   */
  private String custId;
  /**
   * 客戶登入IP
   */
  private String loginIp;
  /**
   * 通道別
   */
  private String loginway;
  /**
   * 下一個方法代號
   */
  private String fnct;
  /**
   * 回應代碼
   */
  private String rtnCode;
  /**
   * 回應訊息
   */
  private String rtnMsg;
  /**
   * 擴充性欄位
   */
  @Default
  private Map<String, String> jsonObj = new HashMap<>();

  UserDetails pass() {
    this.setAccountNonExpired(true);
    this.setAccountNonLocked(true);
    this.setEnabled(true);
    this.setCredentialsNonExpired(true);
    return this;
  }
}
