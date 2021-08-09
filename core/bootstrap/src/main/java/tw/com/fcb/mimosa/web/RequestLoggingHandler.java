package tw.com.fcb.mimosa.web;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.TRACE;
import static org.springframework.http.HttpMethod.resolve;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;
import static org.springframework.web.context.request.async.WebAsyncUtils.getAsyncManager;
import static tw.com.fcb.mimosa.web.RequestLogging.REQUEST_LOGGING;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 不使用 spring 的 CommonsRequestLoggingFilter 原因大概就是因為沒有 handler 資訊跟不能在 {@link
 * org.springframework.web.filter.CommonsRequestLoggingFilter#afterRequest(HttpServletRequest,
 * String)} 中針對 response 中在獲取更多點訊息 log 出來
 *
 * @see org.springframework.web.filter.CommonsRequestLoggingFilter
 */
@Slf4j
@RequiredArgsConstructor
public class RequestLoggingHandler extends HandlerInterceptorAdapter {

  protected static final List<HttpMethod> IGNORING_METHODS = List.of(OPTIONS, TRACE);

  final WebProperties web;

  protected boolean shouldLog(HttpServletRequest request) {
    return !IGNORING_METHODS.contains(resolve(request.getMethod()))
        && !getAsyncManager(request).hasConcurrentResult();
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (shouldLog(request)) {
      var logging = new RequestLogging();
      request.setAttribute(REQUEST_LOGGING, logging);
      if (web.includeCostTime) {
        var watch = new StopWatch();
        watch.start();
        logging.setWatch(watch);
      }
      var message = new StringBuilder()
          .append(request.getMethod())
          .append(" ")
          .append(url(request))
          .append(" (")
          .append(Math.max(request.getContentLengthLong(), 0))
          .append("-byte body) --> ")
          .append(handler);
      if (web.includeClientInfo) {
        message.append(", client=").append(request.getRemoteAddr());
      }
      if (web.includeHeaders) {
        message.append(", headers=").append(new ServletServerHttpRequest(request).getHeaders());
      }
      log.info("{}", message);
    }
    return super.preHandle(request, response, handler);
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
    if (modelAndView != null) {
      ofNullable(request.getAttribute(REQUEST_LOGGING))
          .filter(RequestLogging.class::isInstance)
          .map(RequestLogging.class::cast)
          .ifPresent(logging -> logging.setModel(modelAndView.getModel()));
    }
    super.postHandle(request, response, handler, modelAndView);
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    ofNullable(request.getAttribute(REQUEST_LOGGING))
        .filter(RequestLogging.class::isInstance)
        .map(RequestLogging.class::cast)
        .ifPresent(
            logging -> {
              var message = new StringBuilder()
                  .append(HttpStatus.valueOf(response.getStatus()))
                  .append("  <-- ")
                  .append(handler);
              var model = logging.getModel();
              if (!isEmpty(model)) {
                message.append(", model=").append(model);
              }
              var watch = logging.getWatch();
              if (watch != null) {
                if (watch.isRunning()) {
                  watch.stop();
                }
                message.append(", cost=").append(watch.getTotalTimeMillis()).append("ms");
              }
              log.info("{}", message);
            });
    super.afterCompletion(request, response, handler, ex);
  }

  private String url(HttpServletRequest request) {
    StringBuilder message = new StringBuilder();
    message.append(request.getRequestURI());
    if (web.includeQueryString && hasText(request.getQueryString())) {
      message.append("?").append(request.getQueryString());
    }
    return message.toString();
  }
}
