package tw.com.fcb.mimosa.domain.t9n;

/**
 * 翻譯結果
 *
 * @author Matt Ho
 */
public interface Translated extends Term {

  /**
   * 翻譯
   *
   * @return
   */
  String getTranslation();
}
