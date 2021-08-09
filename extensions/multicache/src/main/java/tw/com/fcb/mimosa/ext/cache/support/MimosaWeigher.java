package tw.com.fcb.mimosa.ext.cache.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import com.github.benmanes.caffeine.cache.Weigher;

/** @author Matt Ho */
class MimosaWeigher implements Weigher, Serializable {

  static MimosaWeigher INSTANCE = new MimosaWeigher();

  @Override
  public int weigh(Object key, Object value) {
    if (value == null) {
      return 0;
    }
    if (value instanceof Optional) {
      var opt = (Optional<?>) value;
      if (opt.isEmpty()) {
        return 0;
      }
      return weigh(key, opt.get());
    }
    if (value instanceof Collection) {
      return ((Collection<?>) value).size();
    }
    if (value instanceof Map) {
      return ((Map<?, ?>) value).size();
    }
    return 1;
  }
}
