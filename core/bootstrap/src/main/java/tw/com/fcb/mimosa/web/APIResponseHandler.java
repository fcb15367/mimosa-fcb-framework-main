package tw.com.fcb.mimosa.web;

import static java.lang.String.format;
import static tw.com.fcb.mimosa.tracing.TraceContext.getBaggageItem;
import static tw.com.fcb.mimosa.tracing.TraceContext.getTraceId;
import static tw.com.fcb.mimosa.tracing.TraceContext.setBaggageItem;

import java.util.function.UnaryOperator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.http.APIResponse;

/**
 * 透過這隻 AOP 來產生並注入 {@link APIResponse} 的部分欄位內容, 算是客戶專屬的客製化邏輯
 *
 * @author Matt Ho
 */
@Slf4j
@RequiredArgsConstructor
public class APIResponseHandler implements UnaryOperator<APIResponse<?>> {

  private static final String SOURCE_ID = "sourceId";
  private static final String TRANSACTION_ID = "transactionId";

  @NonNull
  final String responseId;
  final boolean strict;

  @Override
  public APIResponse<?> apply(@NonNull APIResponse<?> response) {
    return apply(response, this.strict);
  }

  public APIResponse<?> apply(@NonNull APIResponse<?> response, boolean strict) {
    log.debug("injecting fields into {}", response);
    if (response.getResponseId() == null) {
      log.debug("response.responseId not found, setting value for '{}'", responseId);
      response.setResponseId(responseId);
    }
    if (response.getSourceId() == null) {
      var sourceId = getSourceId(strict);
      log.debug("response.sourceId not found, setting value for '{}'", sourceId);
      response.setSourceId(sourceId);
    }
    if (response.getTransactionId() == null) {
      var transactionId = getTransactionId(strict);
      log.debug("response.transactionId not found, setting value for '{}'", transactionId);
      response.setTransactionId(transactionId);
    }
    return response;
  }

  /** 這段是一銀提出的需求, 若沒 API 呼叫者沒提供 TransactionId, 則將自身的 TraceId 塞入該欄位中 */
  public void handleTransactionIdIfMissing() {
    if (getBaggageItem(TRANSACTION_ID).isEmpty()) {
      getTraceId().ifPresent(traceId -> setBaggageItem(TRANSACTION_ID, traceId));
    }
  }

  /**
   * @throws RuntimeException if strict mode enabled and any of sourceId or transactionId is illegal
   */
  void validateStrict() throws RuntimeException {
    if (this.strict) {
      getTransactionId(true);
      getSourceId(true);
    }
  }

  private String getTransactionId(boolean strict) {
    return getBaggageItem(TRANSACTION_ID)
        .orElseGet(
            () -> {
              if (strict) {
                throw new IllegalStateException(
                    format(
                        "Neither '%s' from header nor TraceId from OpenTracing are found, consider adding 'opentracing.jaeger.enabled=true' in your properties.",
                        TRANSACTION_ID));
              }
              return null;
            });
  }

  private String getSourceId(boolean strict) {
    return getBaggageItem(SOURCE_ID)
        .orElseGet(
            () -> {
              if (strict) {
                throw new IllegalArgumentException(
                    format("Request header '%s' is required in strict mode", SOURCE_ID));
              }
              return null;
            });
  }
}
