package tw.com.fcb.mimosa.web;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 從request body 取出 "header" 放進 request header
 * <p>
 * 多次取出request body的方法
 * 參考{@link AppendableHeaderRequestWrapper}
 * </p>
 *
 * @author Jason Wu
 */
@Slf4j
@RequiredArgsConstructor
public class APIRequestFilter extends OncePerRequestFilter {

  final ObjectMapper mapper;

  final int bufferSize;

  final String bodyEncoding;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    boolean isFirstRequest = !isAsyncDispatch(request);
    if (isFirstRequest && !(request instanceof AppendableHeaderRequestWrapper)) {
      AppendableHeaderRequestWrapper requestToUse = new AppendableHeaderRequestWrapper(request, bufferSize, bodyEncoding);
      try {
        log.debug("request body:\n{}", requestToUse.getRequestBody());
        var root = mapper.readTree(requestToUse.getRequestBody());
        if (root.has("header")) {
          var bodyHeader = root.get("header");
          Iterator<String> keys = bodyHeader.fieldNames();
          while (keys.hasNext()) {
            String key = keys.next();
            requestToUse.addHeader(key, bodyHeader.get(key).asText());
          }
        }
      } catch (Exception e) {
        log.debug("can not parse request body : {} ", e.getMessage());
      } finally {
        filterChain.doFilter(requestToUse, response);
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
