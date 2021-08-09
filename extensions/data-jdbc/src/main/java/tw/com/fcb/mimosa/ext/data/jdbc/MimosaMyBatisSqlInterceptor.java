package tw.com.fcb.mimosa.ext.data.jdbc;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.lang.Nullable;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 這支程式會將要執行的 sql 取發佈出去
 *
 * @author Matt Ho
 */
@Intercepts({
    @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
    @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,
        ResultHandler.class })
})
@Slf4j
public class MimosaMyBatisSqlInterceptor implements Interceptor {

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    log.trace(
        "start intercepting mybatis bound sql for {}.{}",
        invocation.getTarget().getClass().getName(),
        invocation.getMethod().getName());
    try {
      var statement = (MappedStatement) invocation.getArgs()[0];
      var parameter = Optional.of(invocation.getArgs())
          .filter(args -> args.length > 1)
          .map(args -> args[1])
          .orElse(null);
      var configuration = statement.getConfiguration();
      var boundSql = statement.getBoundSql(parameter);
      var sqlToUse = SqlToUse.builder()
          .originalSql(boundSql.getSql())
          .sql(boundSql.getSql().replaceAll("[\\s]+", " "))
          .parameters(collectSqlParameters(configuration, boundSql))
          .build();
      log.trace("setting sql-to-use to context holder: {}", sqlToUse);
      SqlToUseHolder.setContext(sqlToUse);
    } catch (Exception e) {
      log.error("Failed to set SqlToUse to SqlToUseHolder.", e);
    }
    return invocation.proceed();
  }

  List<SqlParameter> collectSqlParameters(Configuration configuration, BoundSql boundSql) {
    var parameter = boundSql.getParameterObject();
    var mappings = boundSql.getParameterMappings();
    if (parameter == null || mappings.size() == 0) {
      return emptyList();
    }
    var registry = configuration.getTypeHandlerRegistry();
    if (registry.hasTypeHandler(parameter.getClass())) {
      log.trace(
          "found registered type-handler for parameter class: {}", parameter.getClass().getName());
      log.trace("building sql-parameter directly for the parameter: {}", parameter);
      // 這邊代表 mybatis mapper 的 parameterType 是一個可以直接被處理的物件
      // 如 int, long 等, 並非是 object 或 map 這些多包一層的物件
      return mappings.stream()
          .map(mapping -> buildSqlParameter(mapping, parameter))
          .collect(toList());
    }
    log.trace("creating meta-object for parameter: {}", parameter);
    var meta = configuration.newMetaObject(parameter);
    return mappings.stream()
        .map(mapping -> buildSqlParameter(mapping, meta, boundSql))
        .collect(toList());
  }

  SqlParameter buildSqlParameter(
      @NonNull ParameterMapping mapping, @NonNull MetaObject meta, @NonNull BoundSql boundSql) {
    var property = mapping.getProperty();
    if (meta.hasGetter(property)) {
      var value = meta.getValue(property);
      log.trace(
          "found getter for property [{}] in meta-object, building sql-parameter using its value: {}",
          property,
          value);
      return buildSqlParameter(mapping, value);
    }
    if (boundSql.hasAdditionalParameter(property)) {
      var value = boundSql.getAdditionalParameter(property);
      log.trace(
          "found additional parameter for property [{}] in bound-sql, building sql-parameter using its value: {}",
          property,
          value);
      return buildSqlParameter(mapping, value);
    }
    log.trace("building sql-parameter for property [{}] with 'null' value", property);
    return buildSqlParameter(mapping, null);
  }

  SqlParameter buildSqlParameter(@NonNull ParameterMapping mapping, @Nullable Object value) {
    return SqlParameter.builder()
        .name(mapping.getProperty())
        .typeName(mapping.getJdbcTypeName())
        .value(value)
        .build();
  }
}
