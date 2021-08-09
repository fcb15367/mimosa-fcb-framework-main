package tw.com.fcb.mimosa.domain.t9n;

import javax.validation.constraints.NotBlank;

/**
 * 詞彙
 *
 * @author Matt Ho
 */
public interface Term {

  /**
   * 大分類
   *
   * @return
   */
  @NotBlank
  String getCategory();

  /**
   * 代碼
   *
   * @return
   */
  @NotBlank
  String getCode();
}
