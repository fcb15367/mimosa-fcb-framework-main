package tw.com.fcb.mimosa.ext.data.jdbc.support;

import org.springframework.dao.DataAccessException;

/** @author Matt Ho */
@FunctionalInterface
public interface DataAccessOperation<T> {

  T execute() throws DataAccessException;
}
