package tw.com.fcb.mimosa.tracing;

import static tw.com.fcb.mimosa.tracing.TraceContext.setBaggageItem;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import tw.com.fcb.mimosa.tracing.TracingProperties.Propagation;
import tw.com.fcb.mimosa.web.APIResponseHandler;

/**
 * 將自定義要被傳播的 header 放到 span context 中
 *
 * @author Matt Ho
 */
@Builder
@Slf4j
@RequiredArgsConstructor
public class ExtraPropagationFilter extends OncePerRequestFilter {

  @NonNull
  final List<Propagation> extras;
  @NonNull
  final APIResponseHandler responseHandler;

  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      StreamEx.of(extras)
          .mapToEntry(Propagation::getBaggage, p -> request.getHeader(p.getHeader()))
          .filterValues(Objects::nonNull)
          .forEach(e -> setBaggageItem(e.getKey(), e.getValue()));
    } catch (Exception e) {
      log.warn(
          "Failed to setting extra-propagation from http-header and span-context: {}", extras, e);
    }
    responseHandler.handleTransactionIdIfMissing();
    filterChain.doFilter(request, response);
  }
}
