package tw.com.fcb.mimosa.ext.data.jdbc;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import org.mapstruct.Mapper;

import lombok.Builder;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * 封裝被執行的 Sql 的 parameter 物件
 *
 * @author Matt Ho
 */
@Value
@Builder
public class SqlParameter {

  @Mapper(componentModel = "default", builder = @org.mapstruct.Builder)
  interface SqlParameterMapper {
    SqlParameter clone(SqlParameter source);

    default String map(Optional<String> optional) {
      return optional.orElse(null);
    }
  }

  /** The name of the parameter, if any */
  String name;

  /** The value of the parameter */
  @NonFinal
  @Setter
  Object value;

  /** Resolve the standard type name for the given SQL type, if possible. */
  String typeName;

  public Optional<String> getTypeName() {
    return ofNullable(typeName);
  }

  public Optional<Object> getValue() {
    return ofNullable(value);
  }
}
