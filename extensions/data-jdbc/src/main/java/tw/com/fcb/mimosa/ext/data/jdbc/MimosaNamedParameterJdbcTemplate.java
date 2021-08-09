package tw.com.fcb.mimosa.ext.data.jdbc;

import static java.util.Optional.ofNullable;
import static org.springframework.jdbc.support.JdbcUtils.resolveTypeName;

import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;

/** @author Matt Ho */
@Slf4j
public class MimosaNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {

  public MimosaNamedParameterJdbcTemplate(DataSource dataSource) {
    super(dataSource);
  }

  public MimosaNamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
    super(classicJdbcTemplate);
  }

  @Override
  protected PreparedStatementCreatorFactory getPreparedStatementCreatorFactory(
      ParsedSql parsedSql, SqlParameterSource paramSource) {
    var sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
    var declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);
    try {
      SqlToUseHolder.setContext(
          SqlToUse.builder()
              .originalSql(parsedSql.toString())
              .sql(sqlToUse)
              .parameters(
                  ofNullable(paramSource.getParameterNames())
                      .map(
                          names -> StreamEx.of(names)
                              .map(name -> buildSqlParameter(name, paramSource))
                              .toList())
                      .orElseGet(Collections::emptyList))
              .build());
    } catch (Exception e) {
      log.error("Failed to set SqlToUse to SqlToUseHolder.", e);
    }
    return new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);
  }

  SqlParameter buildSqlParameter(String name, SqlParameterSource source) {
    return SqlParameter.builder()
        .name(name)
        .typeName(resolveTypeName(source.getSqlType(name)))
        .value(source.getValue(name))
        .build();
  }
}
