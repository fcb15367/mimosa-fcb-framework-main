package tw.com.fcb.mimosa.domain.t9n;

import java.util.Optional;

import lombok.NonNull;

/**
 * 翻譯服務
 *
 * @author Matt Ho
 */
public interface TranslationService {

  /**
   * @param term
   * @return Optional.empty() if term translate failed
   */
  Optional<Translated> translate(@NonNull Term term);
}
