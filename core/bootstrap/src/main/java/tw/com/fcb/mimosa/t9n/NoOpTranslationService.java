package tw.com.fcb.mimosa.t9n;

import java.util.Optional;

import tw.com.fcb.mimosa.domain.t9n.Term;
import tw.com.fcb.mimosa.domain.t9n.Translated;
import tw.com.fcb.mimosa.domain.t9n.TranslationService;

/** @author Matt Ho */
class NoOpTranslationService implements TranslationService {

  @Override
  public Optional<Translated> translate(Term term) {
    return Optional.empty();
  }
}
