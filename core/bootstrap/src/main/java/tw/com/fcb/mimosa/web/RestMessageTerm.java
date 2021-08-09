package tw.com.fcb.mimosa.web;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tw.com.fcb.mimosa.domain.t9n.Term;

/**
 * 收集底層用到的 message
 *
 * @author Matt Ho
 */
@RequiredArgsConstructor
@Getter
enum RestMessageTerm implements Term {
  UNKNOWN_ERROR("MD", "MD-S-500-00"),
  CONSTRAINT_VIOLATION("MD", "MD-S-013-00"),
  BIND_EXCEPTION("MD", "MD-S-014-00"),
  METHOD_ARGUMENT_NOT_VALID("MD", "MD-S-016-00"),
  PROPERTY_ACCESS_ERROR("MD", "MD-S-017-00"),
  DB_ACTION_ERROR("MD", "MD-S-018-00"),
  JSON_MAPPING_ERROR("MD", "MD-S-019-00"),
  DATA_OUT_OF_DATE("MD", "MD-S-020-00"),
  ;

  final String category;

  final String code;
}
