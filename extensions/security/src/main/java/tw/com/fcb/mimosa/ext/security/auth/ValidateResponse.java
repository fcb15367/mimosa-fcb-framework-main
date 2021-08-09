package tw.com.fcb.mimosa.ext.security.auth;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

/** 規格來自: Token驗證規格 (002).doc */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateResponse {

  public static final String SUCCESS_RTN_CODE = "0000";

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
  @Singular("jsonObj")
  private Map<String, String> jsonObj;
}
