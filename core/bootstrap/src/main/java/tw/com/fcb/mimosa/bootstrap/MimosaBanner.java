package tw.com.fcb.mimosa.bootstrap;

import static com.google.common.base.Strings.padEnd;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

import java.io.PrintStream;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;

import tw.com.fcb.mimosa.MimosaVersion;

/**
 * 在啟動時進行高大上的輸出
 *
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
public class MimosaBanner implements Banner {

  static String APPLICATION_NAME = "spring.application.name"; // only this one is define by Spring
  static String APPLICATION_VERSION = "spring.application.version";

  static String FALLBACK_APPLICATION_NAME = "info.application.name";
  static String FALLBACK_APPLICATION_VERSION = "info.application.version";

  @Override
  public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {

    var appName = getProperty(environment, APPLICATION_NAME, FALLBACK_APPLICATION_NAME, null);
    var appVersion = getProperty(environment, APPLICATION_VERSION, FALLBACK_APPLICATION_VERSION, null);
    var mimosaVersion = ofNullable(MimosaVersion.getVersion()).map(v -> "Mimosa " + v).orElse(null);
    var info = Stream.of(appName, appVersion, mimosaVersion)
        .filter(Objects::nonNull)
        .collect(joining(" :: "));

    // 沒打算維護下面的圖案...
    out.println(
        AnsiOutput.toString(
            AnsiColor.GREEN,
            "                                   .@@%*                   *%.                  \n"
                + "                                    .@@@@@@,            .&@@&.                  \n"
                + "                                      #@@@@@@(        .&@@@@/                   \n"
                + "                      ,@@.             .(@@@@@&      *@@@@@#                    \n"
                + "                     .@@@@.       .        ./%&.    .@@@@@/      /@*            \n"
                + "                     /@@@@#     ,@@,            ,@.  &@%/    .&@@@&             \n"
                + "                     (@@@@@    *@@@&(@@@@@@@@@%/. ,&    .  ,@@@@@@              \n"
                + " ,@@@@@@@@@@#*       .@@@@&   .@@@@@%@@@@@&@@@@@@* .#/    #@@@@@#               \n"
                + "    *&@@@@@@@@@@/      &@%.   (@@@@@    .&@@@.       .&. ,@@@@#.     .(&@*      \n"
                + "        .,/#%##*.  /&%*.  ,  ./@@&@(,../@@@&&(%#%%(*. .%*.,.     /@@@@@@,       \n"
                + "                   ... .*&&*,  (@@*.,.(@@@@@%@@@@@%%@&.,/%    .&@@@@@@(         \n"
                + "             .#@@@@@@@/  .. .#&/.   , %@@@@((%##%@@&   , ,&. /@@@@@&, .         \n"
                + "          .&@@@@@@@@/    .,/((,  (&,  /@@/    ,@@@@@,,,.  .@,,*,.       .,/     \n"
                + "        .&@@@@&(* .   #@@@@@@@.     ,&%.     ,@@@@@&@@@@@# .&      .#@@@@@,     \n"
                + "                   *@@@@@@@&.    ,(&@( .%(.&@&@@@@%@@@#.    ,&   *@@@@@@&.      \n"
                + "                 .@@@@@&*     /@@@@@@     .&( *(.            *& ,@@@@@(.        \n"
                + "                            (@@@@@@#         *@. ./&@@@@@@@@/ (/                \n"
                + "                          .%&&&@%.   *@@@@@@/  .@&@@@@@@@@@(  .&,               \n"
                + "                         .@&(,.    (@@@@@@@.      %(           *#               \n"
                + "                                 .@@@@@#,           #.          &.              \n"
                + "                                                                (*              \n",
            AnsiColor.BRIGHT_MAGENTA,
            padEnd(format("First Bank :: %s", info), 60, ' ')));

    out.println();
  }

  String getProperty(Environment environment, String key, String fallbackKey, String defaultValue) {
    return ofNullable(environment.getProperty(key))
        .or(() -> ofNullable(environment.getProperty(fallbackKey)))
        .orElse(defaultValue);
  }
}
