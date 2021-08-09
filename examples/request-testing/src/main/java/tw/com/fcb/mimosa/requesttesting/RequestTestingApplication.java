package tw.com.fcb.mimosa.requesttesting;

import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * 需要與examples/security一起啟動，因為模擬驗證伺服器在security
 *
 * <p>
 * 測試使用 POST http://localhost:8081
 * </p>
 * <p>
 * body放{"header":{"transactionId":"body transactionId","sourceId":"body sourceId","clientIp":"body ip"},"body":{"name":"123"}}
 * </p>
 *
 * @author Jason Wu
 */
@MimosaBootstrap
public class RequestTestingApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(RequestTestingApplication.class, args);
  }
}
