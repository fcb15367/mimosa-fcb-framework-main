package tw.com.fcb.mimosa.ext.data.jdbc;

import java.util.List;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * sql 執行前, 被處理過後的一些資訊
 *
 * @author Matt Ho
 */
@Value
@Builder
public class SqlToUse {

  /** 在 repository 定義的 sql */
  @NonNull
  final String originalSql;

  /** 被拿去執行的 sql */
  @NonNull
  final String sql;

  /** prepared statements sql 中定義的變數 */
  @NonNull
  List<SqlParameter> parameters;
}
