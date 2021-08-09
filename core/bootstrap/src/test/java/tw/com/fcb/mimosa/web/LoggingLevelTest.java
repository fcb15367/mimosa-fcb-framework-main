package tw.com.fcb.mimosa.web;

import static org.assertj.core.api.Assertions.assertThat;
import static tw.com.fcb.mimosa.http.Slf4jLevel.DEBUG;
import static tw.com.fcb.mimosa.http.Slf4jLevel.ERROR;
import static tw.com.fcb.mimosa.http.Slf4jLevel.INFO;
import static tw.com.fcb.mimosa.http.Slf4jLevel.TRACE;
import static tw.com.fcb.mimosa.http.Slf4jLevel.WARN;
import static tw.com.fcb.mimosa.web.CatchError.LoggingLevel.OverlayStrategy.HIGHEST;
import static tw.com.fcb.mimosa.web.CatchError.LoggingLevel.OverlayStrategy.LOWEST;
import static tw.com.fcb.mimosa.web.RestMessageTerm.DATA_OUT_OF_DATE;
import static tw.com.fcb.mimosa.web.RestMessageTerm.JSON_MAPPING_ERROR;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tw.com.fcb.mimosa.http.APIError;
import tw.com.fcb.mimosa.http.APIErrorException;
import tw.com.fcb.mimosa.http.APIErrorT9n;
import tw.com.fcb.mimosa.http.APIErrorT9nException;
import tw.com.fcb.mimosa.web.CatchError.LoggingLevel;

/** @author Matt Ho */
@DisplayName("測試例外抓取的 LoggingLevel 邏輯")
class LoggingLevelTest {

  LoggingLevel expected;

  @BeforeEach
  void init() {
    expected = new LoggingLevel();
    expected.defaultLevel = TRACE;
    expected.overlayStrategy = HIGHEST;
    expected.exact = Map.of(
        INFO,
        List.of(JSON_MAPPING_ERROR.code),
        DEBUG,
        List.of(JSON_MAPPING_ERROR.code),
        TRACE,
        List.of(JSON_MAPPING_ERROR.code));
    expected.regex = Map.of(
        WARN,
        List.of("^MD-B-.+$"),
        ERROR,
        List.of("^MD-S-.+$"),
        INFO,
        List.of("^MD-G-.+$", "^MD-.+$"));
    expected.compile();
  }

  @Test
  @DisplayName("測試 Default Level")
  void testDefaultLevel() {
    var code = "1234567";
    var t = new IllegalArgumentException();
    assertThat(expected.findLevel(code, t)).isEqualTo(expected.defaultLevel);
  }

  @Test
  @DisplayName("測試 Exact Level")
  void testExactLevel() {
    var code = JSON_MAPPING_ERROR.code;
    var t = new IllegalArgumentException();
    assertThat(expected.findLevel(code, t)).isEqualTo(INFO);

    expected.overlayStrategy = LOWEST;
    assertThat(expected.findLevel(code, t)).isEqualTo(TRACE);
  }

  @Test
  @DisplayName("測試 Regex Level")
  void testRegexLevel() {
    var code = DATA_OUT_OF_DATE.code;
    var t = new IllegalArgumentException();
    assertThat(expected.findLevel(code, t)).isEqualTo(ERROR);

    expected.overlayStrategy = LOWEST;
    assertThat(expected.findLevel(code, t)).isEqualTo(INFO);
  }

  @Test
  @DisplayName("測試 Specified Level")
  void testSpecifiedLevel() {
    var code = "1234567";
    var t = APIErrorException.apiError(APIError.builder().code("1234567").build())
        .get()
        .setLoggingLevel(DEBUG);
    assertThat(expected.findLevel(code, t)).isEqualTo(t.getLoggingLevel());

    var code2 = JSON_MAPPING_ERROR.code;
    var t2 = new APIErrorT9nException(APIErrorT9n.builder().term(JSON_MAPPING_ERROR).build())
        .setLoggingLevel(DEBUG);
    assertThat(expected.findLevel(code2, t2)).isEqualTo(t.getLoggingLevel());
  }
}
