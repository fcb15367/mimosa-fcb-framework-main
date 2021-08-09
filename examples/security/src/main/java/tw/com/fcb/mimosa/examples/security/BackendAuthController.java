package tw.com.fcb.mimosa.examples.security;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static tw.com.fcb.mimosa.ext.security.auth.ValidateResponse.SUCCESS_RTN_CODE;
import static tw.com.fcb.mimosa.tracing.TraceContext.getJaegerSpanContext;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map.Entry;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.fcb.mimosa.ext.security.auth.ValidateRequest;
import tw.com.fcb.mimosa.ext.security.auth.ValidateResponse;
import tw.com.fcb.mimosa.tracing.TraceContext;

@RestController
@RequestMapping("/backend-auth")
@Tag(name = "Backend Authentication", description = "模擬客戶的後端認證服務")
public class BackendAuthController {

  @PostMapping("/validateToken")
  public ValidateResponse validateToken(@NonNull @Validated @RequestBody ValidateRequest request) {
    if (request.getToken().length() <= 3) { // 自訂義的規則, 目的只是讓我們可以測試正反情境, 非客戶真實的規則
      return errorResponse();
    }
    return successResponse();
  }

  private ValidateResponse successResponse() {
    return ValidateResponse.builder()
        .custId("A1231231230")
        .loginIp("192.168.24.1")
        .loginway("MB")
        .fnct("F0201")
        .rtnCode(SUCCESS_RTN_CODE)
        .rtnMsg("OK")
        .jsonObj("birthday", "1988-08-08")
        // 以下是為了方便 debug, 把 opentracing 的一些資訊也帶著
        .jsonObj("traceId", TraceContext.getTraceId().orElse(null))
        .jsonObj(
            getJaegerSpanContext()
                .map(
                    context -> stream(context.baggageItems().spliterator(), false)
                        .collect(toMap(Entry::getKey, Entry::getValue)))
                .orElseGet(HashMap::new))
        .build();
  }

  private ValidateResponse errorResponse() {
    return ValidateResponse.builder()
        .custId("")
        .loginIp("")
        .loginway("MB")
        .fnct("F0201")
        .rtnCode("9999")
        .rtnMsg("Token長度需>3")
        // 以下是為了方便 debug, 把 opentracing 的一些資訊也帶著
        .jsonObj("traceId", TraceContext.getTraceId().orElse(null))
        .jsonObj(
            getJaegerSpanContext()
                .map(
                    context -> stream(context.baggageItems().spliterator(), false)
                        .collect(toMap(Entry::getKey, Entry::getValue)))
                .orElseGet(HashMap::new))
        .build();
  }
}
