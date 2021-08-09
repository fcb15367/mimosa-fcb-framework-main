package tw.com.fcb.mimosa.unittesting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tw.com.fcb.mimosa.http.APIErrorException;
import tw.com.fcb.mimosa.http.APIResponse;
import tw.com.fcb.mimosa.tracing.TraceContext;

/**
 * 這隻提供 {@link APIResponse} 在各種情境下的測試
 *
 * @author Matt Ho
 */
@RequestMapping("/api/response")
@RestController
public class APIResponseController {

  @PostMapping("/header")
  public APIResponse<String> header(@RequestBody String text) {
    return APIResponse.success(text);
  }

  @PostMapping("/apiresponse-error")
  public APIResponse<String> responseError() {
    return APIResponse.error(err -> err.code("SOME_ERROR_CODE").message("Should not reach here!!"));
  }

  @PostMapping("/throw-error")
  public void error() {
    throw new APIErrorException(
        err -> err.code("SOME_ERROR_CODE").message("Should not reach here!!"));
  }

  @PostMapping("/runtime-exception")
  public void runtimeException() {
    throw new RuntimeException();
  }

}
