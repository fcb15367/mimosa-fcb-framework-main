package tw.com.fcb.mimosa.tracing;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static tw.com.fcb.mimosa.tracing.TracingProperties.PrintStyle.TO_STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/** @author Matt Ho */
@Data
public class TracingProperties {

  Traced traced = new Traced();
  ExtraConfig extra = new ExtraConfig();

  @Data
  public class Traced {
    boolean enabled = true;
    String component = "@traced";

    /** 是否要 trace arguments, 若開啟勢必會有效能損失 */
    boolean arguments = true;

    TimeUnit timeUnit = MILLISECONDS;
    PrintStyle style = TO_STRING;
  }

  @RequiredArgsConstructor
  public enum PrintStyle {
    NONE(traces -> null),
    TO_STRING(Traces::toString),
    SHORT_SUMMARY(Traces::shortSummary),
    PRETTY_PRINT(Traces::prettyPrint),
    ;

    private final Function<Traces, String> formatter;

    String format(Traces tasks) {
      return formatter.apply(tasks);
    }
  }

  @Data
  public static class ExtraConfig {
    List<String> fields = new ArrayList<>();
    Map<String, List<String>> prefixedFields = new HashMap<>();

    public List<Propagation> getPropagation() {
      return Stream.concat(
          fields.stream().map(Propagation::of),
          prefixedFields.entrySet().stream()
              .flatMap(e -> Propagation.of(e.getKey(), e.getValue())))
          .collect(toList());
    }
  }

  @Value
  @AllArgsConstructor
  public static class Propagation {
    static Propagation of(@NonNull String field) {
      return new Propagation(field, field);
    }

    static Stream<Propagation> of(@NonNull String prefix, @NonNull List<String> fields) {
      return fields.stream().map(field -> new Propagation(prefix + field, field));
    }

    /** 從 http header 取值的 key */
    @NonNull
    String header;
    /** 要放在 opentracing 的 span-context 被傳播的 baggage-key */
    @NonNull
    String baggage;
  }
}
