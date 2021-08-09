package tw.com.fcb.mimosa.web;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getThrowableList;
import static org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace.ALWAYS;
import static org.springframework.http.HttpStatus.OK;
import static tw.com.fcb.mimosa.http.APIResponse.error;
import static tw.com.fcb.mimosa.web.RestMessageTerm.BIND_EXCEPTION;
import static tw.com.fcb.mimosa.web.RestMessageTerm.CONSTRAINT_VIOLATION;
import static tw.com.fcb.mimosa.web.RestMessageTerm.DATA_OUT_OF_DATE;
import static tw.com.fcb.mimosa.web.RestMessageTerm.DB_ACTION_ERROR;
import static tw.com.fcb.mimosa.web.RestMessageTerm.JSON_MAPPING_ERROR;
import static tw.com.fcb.mimosa.web.RestMessageTerm.METHOD_ARGUMENT_NOT_VALID;
import static tw.com.fcb.mimosa.web.RestMessageTerm.PROPERTY_ACCESS_ERROR;
import static tw.com.fcb.mimosa.web.RestMessageTerm.UNKNOWN_ERROR;

import java.beans.PropertyChangeEvent;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.PropertyAccessException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeStacktrace;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tw.com.fcb.mimosa.domain.t9n.Term;
import tw.com.fcb.mimosa.domain.t9n.Translated;
import tw.com.fcb.mimosa.domain.t9n.TranslationService;
import tw.com.fcb.mimosa.http.APIError;
import tw.com.fcb.mimosa.http.APIErrorDetail;
import tw.com.fcb.mimosa.http.APIErrorException;
import tw.com.fcb.mimosa.http.APIErrorT9nException;
import tw.com.fcb.mimosa.http.APISQLError;
import tw.com.fcb.mimosa.http.APIValidationError;
import tw.com.fcb.mimosa.web.CatchError.LoggingLevel;

/** @author Matt Ho */
@CrossOrigin
@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public abstract class RestExceptionHandler extends ResponseEntityExceptionHandler
    implements ApplicationEventPublisherAware {

  protected static final String INCLUDE_STACKTRACE = "X-INCLUDE-STACKTRACE";
  final TranslationService translationService;
  final Collection<String> catchCodes;
  final Collection<Class> catchClasses;
  final LoggingLevel loggingLevel;
  final APIResponseHandler responseHandler;

  @Value("${server.error.include-stacktrace:never}")
  private IncludeStacktrace includeStacktrace;

  @Value("${info.application.id:}")
  private String appId;

  private ApplicationEventPublisher publisher;

  public RestExceptionHandler(
      @NonNull CatchError catchError,
      @NonNull TranslationService translationService,
      @NonNull APIResponseHandler responseHandler) {
    this.translationService = translationService;
    this.catchCodes = catchError.getCodes();
    log.info("Initialized web-error-catch for codes: {}", catchCodes);
    this.catchClasses = catchError.getClassesForName();
    log.info("Initialized web-error-catch for classes: {}", catchClasses);
    this.loggingLevel = catchError.getLogging().getLevel().compile();
    log.info("Initialized web-error-catch for logging-level: {}", loggingLevel);
    this.responseHandler = responseHandler;
  }

  /** 這隻的目的是將 parent 的所有例外都封裝成 2xx 風格, 預期傳入的 body 都會是 null */
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return buildErrorResponse(UNKNOWN_ERROR, null, ex, request);
  }

  /** parent 及這隻程式本身所列舉出的例外以外, 都會進這個 method 統一包裝 */
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> defaultExceptionHandler(Exception ex, WebRequest request) {
    return buildErrorResponse(UNKNOWN_ERROR, null, ex, request);
  }

  @ExceptionHandler(APIErrorException.class)
  protected ResponseEntity<Object> handleAPIErrorException(
      APIErrorException ex, WebRequest request) {
    return buildErrorResponse(
        ex.getError().getCode(),
        ex.getError().getMessage(),
        ex.getError().getDetails(),
        ex.getCause(),
        request);
  }

  @ExceptionHandler(APIErrorT9nException.class)
  protected ResponseEntity<Object> handleAPIErrorT9nException(
      APIErrorT9nException ex, WebRequest request) {
    return buildErrorResponse(
        ex.getError().getTerm(), ex.getError().getDetails(), ex.getCause(), request);
  }

  @ExceptionHandler(ConstraintViolationException.class) // 100013 DB
  protected ResponseEntity<Object> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    return buildErrorResponse(
        CONSTRAINT_VIOLATION,
        ex.getConstraintViolations().stream().map(APIValidationError::new).collect(toList()),
        ex,
        request);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(
      BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return buildErrorResponse(
        BIND_EXCEPTION,
        ex.getAllErrors().stream().map(APIValidationError::new).collect(toList()),
        ex,
        request);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return buildErrorResponse(
        METHOD_ARGUMENT_NOT_VALID,
        ex.getBindingResult().getAllErrors().stream()
            .map(APIValidationError::new)
            .collect(toList()),
        ex,
        request);
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return handlePropertyAccessException(ex, request);
  }

  @Override
  protected ResponseEntity<Object> handleConversionNotSupported(
      ConversionNotSupportedException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return handlePropertyAccessException(ex, request);
  }

  @ExceptionHandler(PropertyAccessException.class)
  protected ResponseEntity<Object> handlePropertyAccessException(
      PropertyAccessException ex, WebRequest request) {
    return buildErrorResponse(
        PROPERTY_ACCESS_ERROR,
        List.of(
            APIValidationError.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .object(
                    ofNullable(ex.getPropertyChangeEvent())
                        .map(PropertyChangeEvent::getSource)
                        .map(Object::toString)
                        .orElse(null))
                .field(ex.getPropertyName())
                .rejectedValue(ex.getValue())
                .build()),
        ex,
        request);
  }

  @ExceptionHandler(SQLException.class)
  protected ResponseEntity<Object> handleSQLException(SQLException ex, WebRequest request) {
    return buildErrorResponse(DB_ACTION_ERROR, List.of(new APISQLError(ex)), ex, request);
  }

  @ExceptionHandler(OptimisticLockingFailureException.class)
  protected ResponseEntity<Object> handleOptimisticLockingFailureException(
      OptimisticLockingFailureException ex, WebRequest request) {
    return buildErrorResponse(DATA_OUT_OF_DATE, null, ex, request);
  }

  @ExceptionHandler(DbActionExecutionException.class)
  protected ResponseEntity<Object> handleDbActionExecutionException(
      DbActionExecutionException ex, WebRequest request) {
    return getCause(ex, SQLException.class)
        .map(APISQLError::new)
        .map(detail -> buildErrorResponse(DB_ACTION_ERROR, List.of(detail), ex, request))
        .or(
            () -> getCause(ex, OptimisticLockingFailureException.class)
                .map(cause -> buildErrorResponse(DATA_OUT_OF_DATE, null, ex, request)))
        .orElseGet(() -> buildErrorResponse(DB_ACTION_ERROR, null, ex, request));
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return getCause(ex, JsonMappingException.class)
        .map(this::toAPIErrorDetails)
        .map(details -> buildErrorResponse(JSON_MAPPING_ERROR, details, ex, request))
        .orElseGet(() -> super.handleHttpMessageNotReadable(ex, headers, status, request));
  }

  @ExceptionHandler(JsonMappingException.class)
  protected ResponseEntity<Object> handleJsonMappingException(
      JsonMappingException ex, WebRequest request) {
    return buildErrorResponse(JSON_MAPPING_ERROR, toAPIErrorDetails(ex), ex, request);
  }

  List<APIValidationError> toAPIErrorDetails(@NonNull JsonMappingException ex) {
    var code = UPPER_CAMEL.to(
        LOWER_UNDERSCORE, ex.getClass().getSimpleName().replaceAll("Exception$", ""));
    return ex.getPath().stream()
        .map(
            ref -> APIValidationError.builder()
                .code(code)
                .message(ex.getOriginalMessage())
                .object(ref.getDescription())
                .field(ref.getFieldName())
                .build())
        .collect(toList());
  }

  protected ResponseEntity<Object> buildErrorResponse(
      @NonNull Term term,
      @Nullable List<? extends APIErrorDetail> details,
      @Nullable Throwable ex,
      @NonNull WebRequest request) {
    var message = translationService.translate(term).map(Translated::getTranslation).orElse(null);
    return buildErrorResponse(term.getCode(), message, details, ex, request);
  }

  protected ResponseEntity<Object> buildErrorResponse(
      @NonNull String code,
      @Nullable String message,
      @Nullable List<? extends APIErrorDetail> details,
      @Nullable Throwable ex,
      @NonNull WebRequest request) {
    log.trace(
        "building code=[{}], message=[{}], details=[{}], exception=[{}] to error-response",
        code,
        message,
        details,
        getMessage(ex));
    var error = TracedAPIError.builder()
        .code(code)
        // 為了方便 API Consumer 使用, 不用去‍特別判斷 message 或 details 是否為 null
        // 因此這兩個欄位都給預設的空值而不是 null 才轉 JSON 傳出去
        .message(
            ofNullable(message)
                .or(() -> ofNullable(ex).map(ExceptionUtils::getMessage))
                .orElse(code))
        .details(ofNullable(details).orElseGet(Collections::emptyList))
        .build();
    if (includeStackTrace(request)) {
      ofNullable(ex)
          .map(ExceptionUtils::getRootCauseStackTrace)
          .map(Arrays::asList)
          // traces 原本就不一定會開啟, 且這只是一個方便 debug 的欄位 API Consumer 不應該依賴此欄位做邏輯判斷
          // 因此為了最小化 JSON 內容節省傳輸字元, 當為空時也都不傳出去
          .filter(Predicate.not(List::isEmpty))
          .ifPresent(error::setTraces);
    }
    log.trace("built error-response: {}", error);
    loggingLevel.log(log, error, ex);
    triggerErrorCaught(error, ex);
    return new ResponseEntity<>(
        responseHandler.apply(
            error(error), false // 避免真正的錯誤被 strict 蓋掉, 這邊固定使用 non-strict mode
        ),
        OK);
  }

  void triggerErrorCaught(@NonNull APIError error, @Nullable Throwable t) {
    catchCodes.stream()
        .filter(error.getCode()::equalsIgnoreCase)
        .findAny()
        .map(Object.class::cast)
        .or(
            () -> {
              var throwables = getThrowableList(t);
              return catchClasses.stream()
                  .filter(clazz -> getCause(throwables, clazz).isPresent())
                  .findAny();
            })
        .map(any -> ErrorCaughtEvent.builder().error(error).throwing(t).build())
        .ifPresent(this::publish);
  }

  void publish(ErrorCaughtEvent event) {
    log.trace("publishing error-caught event for {}", event);
    publisher.publishEvent(event);
  }

  protected boolean includeStackTrace(WebRequest request) {
    return includeStacktrace == ALWAYS
        || ofNullable(request.getHeader(INCLUDE_STACKTRACE))
            .map(Boolean::parseBoolean)
            .orElse(false);
  }

  <T extends Throwable> Optional<T> getCause(
      @Nullable Throwable throwable, @NonNull Class<T> expected) {
    return getCause(getThrowableList(throwable), expected);
  }

  /**
   * 在一推 {@code throwables} 中找到符合 {@code expected} 的那個錯誤
   *
   * @param throwables
   * @param expected
   * @param <T>
   * @return
   */
  <T extends Throwable> Optional<T> getCause(
      @NonNull List<Throwable> throwables, @NonNull Class<T> expected) {
    return throwables.stream()
        .filter(cause -> expected.isAssignableFrom(cause.getClass()))
        .map(expected::cast)
        .findFirst();
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }
}
