package tw.com.fcb.mimosa.ext.cache;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

/** @author Matt Ho */
@RequiredArgsConstructor
class PrefixedKeyGenerator implements KeyGenerator {

  final String prefix;

  @Override
  public Object generate(Object target, Method method, Object... params) {
    var sb = new StringBuilder();
    if (!StringUtils.isEmpty(prefix)) {
      sb.append(prefix).append(":");
    }
    sb.append(target.getClass().getName()).append(":");
    sb.append(method.getName()).append(":");
    sb.append("[");
    if (Objects.nonNull(params) && params.length > 0) {
      Stream.of(params).forEach(sb::append);
    }
    sb.append("]");
    return sb.toString();
  }
}
