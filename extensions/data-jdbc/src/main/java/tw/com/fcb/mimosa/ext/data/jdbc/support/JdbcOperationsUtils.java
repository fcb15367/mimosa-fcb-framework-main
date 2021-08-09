package tw.com.fcb.mimosa.ext.data.jdbc.support;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Miscellaneous utility methods for Repository implementations. Useful with any JDBC operations
 * technology.
 *
 * @author Matt Ho
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JdbcOperationsUtils {

  /**
   * 當在下 query 查不到時, 一般來說常見的處理會返回 null 或 {@code Optional.empty()}, 但 Spring 的動作是丟出 {@code
   * EmptyResultDataAccessException}, 因此這個方法是個語法糖協助你將 {@code EmptyResultDataAccessException} 包裝成
   * {@code Optional.empty()}
   *
   * <p>
   * 使用範例如下:
   *
   * <pre>
   * import static tw.com.fcb.mimosa.ext.data.jdbc.support.JdbcOperationsUtils.*;
   *
   * public class MyRepositoryImpl {
   *
   *   final JdbcOperations jdbcOperations;
   *
   *   public Optional<..> findOneByCondition(..) {
   *     return ofNullable(
   *         () ->
   *             jdbcOperations.queryForObject(
   *                 "select * from my_table where ...", MyEntity.class));
   *   }
   * }
   * </pre>
   *
   * @param operation
   * @param <T>
   * @return
   * @throws DataAccessException
   * @see EmptyResultDataAccessException
   */
  public static <T> Optional<T> ofNullable(@NonNull DataAccessOperation<T> operation)
      throws DataAccessException {
    try {
      return Optional.ofNullable(operation.execute());
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }
}
