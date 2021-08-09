package tw.com.fcb.mimosa.ext.security.auth;

/**
 * Backend Auth Client, 用來串接客戶既有的安控模組
 *
 * <p>
 * 規格來自: Token驗證規格 (002).doc
 */
public interface AuthClient {

  /**
   * 網路銀行使用者 Token 驗證 (For Token申請規格第2類)
   *
   * @param request
   * @return
   */
  ValidateResponse validateToken(ValidateRequest request);
}
