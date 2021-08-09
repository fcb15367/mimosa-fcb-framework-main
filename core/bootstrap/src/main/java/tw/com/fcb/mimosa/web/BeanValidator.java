package tw.com.fcb.mimosa.web;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.validation.DataBinder.DEFAULT_OBJECT_NAME;

import java.util.Optional;

import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.domain.t9n.Translated;
import tw.com.fcb.mimosa.domain.t9n.TranslationService;
import tw.com.fcb.mimosa.http.APIError;
import tw.com.fcb.mimosa.http.APIValidationError;

/**
 * 這支程式整合了 Spring 的 {@link Validator} 及 {@link APIError}, 適合給想要從程式面控制 Java Bean 驗證的邏輯使用
 *
 * @see Validator
 * @see APIError
 * @author Matt Ho
 */
@RequiredArgsConstructor
public class BeanValidator {

  @Getter
  @NonNull
  final Validator validator;
  @NonNull
  final TranslationService translationService;

  public Optional<APIError> validate(@Nullable Object target) {
    return validate(target, null);
  }

  public Optional<APIError> validate(@Nullable Object target, @Nullable String objectName) {
    var binder = new DataBinder(target, ofNullable(objectName).orElse(DEFAULT_OBJECT_NAME));
    binder.setValidator(validator);
    binder.validate();
    return toError(binder.getBindingResult());
  }

  public Optional<APIError> toError(@NonNull BindingResult result) {
    if (!result.hasErrors()) {
      return Optional.empty();
    }
    return Optional.of(
        APIError.builder()
            .code(RestMessageTerm.CONSTRAINT_VIOLATION.getCode())
            .message(
                translationService
                    .translate(RestMessageTerm.CONSTRAINT_VIOLATION)
                    .map(Translated::getTranslation)
                    .orElse(null))
            .details(result.getAllErrors().stream().map(APIValidationError::new).collect(toList()))
            .build());
  }
}
