package tw.com.fcb.mimosa.web;

import java.util.Map;

import org.springframework.util.StopWatch;

import lombok.Data;

/**
 * 這只是一個用於貫穿在 {@link RequestLoggingHandler} 中每個 step 之間的物件
 *
 * @author Matt Ho
 */
@Data
class RequestLogging {

  /** Used to cache a request-logging object in request attribute. */
  static final String REQUEST_LOGGING = "MIMOSA_WEB_REQUEST_LOGGING";

  StopWatch watch;
  Map<String, Object> model;
}
