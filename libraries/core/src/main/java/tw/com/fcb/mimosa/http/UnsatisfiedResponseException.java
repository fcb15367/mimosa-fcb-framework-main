package tw.com.fcb.mimosa.http;

import static java.lang.String.format;

import lombok.NonNull;

/**
 * 不滿意的回應, 通常是預期成功, 但實際上失敗
 *
 * @author Matt Ho
 */
public class UnsatisfiedResponseException extends Exception {

  private static final long serialVersionUID = 2794668652644246652L;

  public UnsatisfiedResponseException(@NonNull APIResponse resp) {
    this(format("Unsatisfied API response: %s", resp));
  }

  public UnsatisfiedResponseException(@NonNull String message) {
    super(message);
  }
}
