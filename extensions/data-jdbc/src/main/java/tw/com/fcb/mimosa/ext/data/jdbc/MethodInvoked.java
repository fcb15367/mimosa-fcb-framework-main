package tw.com.fcb.mimosa.ext.data.jdbc;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * 紀錄在 {@link RepositoryAspect} 中調用的 method 相關資訊
 *
 * @author Matt Ho
 */
@Value
@Builder
public class MethodInvoked {

  @NonNull
  Class<?> declaringClass;

  @NonNull
  String name;

  @NonNull
  String simpleName;

  @NonNull
  String fullName;
}
