package tw.com.fcb.mimosa.web;

import java.nio.charset.Charset;

import lombok.Data;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
@Data
public class WebProperties {

  int requestBufferSize = 8192;

  String requestBodyEncoding = "UTF-8";

  boolean enableLog = true;

  /** Web 嚴格模式 */
  boolean strict = true;

  boolean includeQueryString = true;

  boolean includeClientInfo = true;

  boolean includeHeaders = true;

  boolean includeCostTime = true;

  long defaultAsyncTimeout = 60L * 1_000;

  CatchError catchError = new CatchError();
}
