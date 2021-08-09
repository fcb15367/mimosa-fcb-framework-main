package tw.com.fcb.mimosa.redis;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;

/**
 * A helper class to obtain keys for redis
 *
 * @author Matt Ho
 */
public class RedisKey {

  public static String joining(@NonNull String slice, @NonNull String... rest) {
    return Stream.concat(Stream.of(slice), Stream.of(rest))
        .filter(Objects::nonNull)
        .collect(Collectors.joining(":"));
  }
}
