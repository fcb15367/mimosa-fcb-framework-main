package tw.com.fcb.mimosa.web;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 解決HttpServletRequest不能重複讀取body以及不能添加header的問題
 *
 * <p>
 * 多次讀取request body {@link AppendableHeaderRequestWrapper#getRequestBody()}
 * <p>
 * 添加自定義的header {@link AppendableHeaderRequestWrapper#addHeader(String, String)}
 *
 * @author Jason Wu
 */
public class AppendableHeaderRequestWrapper extends HttpServletRequestWrapper {

  private String requestBody;

  private final Map<String, String> customHeaders = new HashMap<>();
  private final Map<String, String[]> parameters = new HashMap<>();

  private final int bufferSize;
  private final String bodyEncoding;

  private boolean parsedParams = false;

  public AppendableHeaderRequestWrapper(HttpServletRequest request, int bufferSize, String bodyEncoding) throws IOException {
    super(request);
    this.bufferSize = bufferSize;
    this.bodyEncoding = bodyEncoding;
    renewBody(request);
  }

  @Override
  public ServletInputStream getInputStream() {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes());

    return new ServletInputStream() {
      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener) {
      }

      @Override
      public int read() {
        return byteArrayInputStream.read();
      }
    };
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(this.getInputStream()));
  }

  private void renewBody(HttpServletRequest request) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    byte[] buffer = new byte[bufferSize];
    int length;
    while ((length = request.getInputStream().read(buffer)) != -1) {
      result.write(buffer, 0, length);
    }
    requestBody = result.toString(bodyEncoding);
  }

  public String getRequestBody() {
    return requestBody;
  }

  public void addHeader(String key, String value) {
    this.customHeaders.put(key, value);
  }

  @Override
  public String getHeader(String key) {
    String headerValue = customHeaders.get(key);
    if (headerValue != null) {
      return headerValue;
    }
    return ((HttpServletRequest) getRequest()).getHeader(key);
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    Set<String> set = new HashSet<>(customHeaders.keySet());
    Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
    while (e.hasMoreElements()) {
      String n = e.nextElement();
      set.add(n);
    }
    return Collections.enumeration(set);
  }

  @Override
  public Enumeration<String> getParameterNames() {
    if (!parsedParams)
      parseParams();

    List<String> result = Collections.list(super.getParameterNames());
    result.addAll(parameters.keySet());

    return Collections.enumeration(result);
  }

  private void parseParams() {
    if (!requestBody.isEmpty()) {
      String[] rps = requestBody.split("&");

      for (String rp : rps) {
        String[] kv = rp.split("=");
        parameters.put(kv[0],
            kv.length > 1 ? new String[] { URLDecoder.decode(kv[1], Charset.forName(bodyEncoding)) } : new String[] { "" });
      }
      parsedParams = true;
    }
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    if (!parsedParams)
      parseParams();

    Map<String, String[]> s = super.getParameterMap();
    return Stream.concat(parameters.entrySet().stream(), s.entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  public String getParameter(String name) {
    return parameters.get(name) != null ? parameters.get(name)[0] : super.getParameter(name);
  }

  @Override
  public String[] getParameterValues(String name) {
    String[] s = super.getParameterValues(name);
    return ArrayUtils.addAll(s, parameters.get(name));
  }
}
