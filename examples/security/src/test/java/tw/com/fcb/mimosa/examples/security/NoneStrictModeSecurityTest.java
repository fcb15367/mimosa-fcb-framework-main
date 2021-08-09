package tw.com.fcb.mimosa.examples.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tw.com.fcb.mimosa.ext.security.SimpleUser;
import tw.com.fcb.mimosa.ext.security.UserDetailsByUsernameService;
import tw.com.fcb.mimosa.ext.security.auth.*;
import tw.com.fcb.mimosa.test.MimosaTest;
import tw.com.fcb.mimosa.test.TestProfile;
import tw.com.fcb.mimosa.test.util.GlobalTracerTestUtil;
import tw.com.fcb.mimosa.tracing.TraceContext;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tw.com.fcb.mimosa.tracing.TraceContext.getJaegerSpanContext;

@Slf4j
@TestProfile
@MimosaTest(properties = { "mimosa.security.strict:false" })
@Timeout(5)
@AutoConfigureMockMvc
class NoneStrictModeSecurityTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserDetailsByUsernameService userDetailsByUsernameService;

  @MockBean
  AuthClient authClient;

  @Autowired
  AuthMapper mapper;

  String expectedResponseId = "test";

  @BeforeAll
  @AfterAll
  static void reset() {
    GlobalTracerTestUtil.resetGlobalTracer();
  }

  @Test
  @DisplayName("未送 bearer -> 擋掉, response 驗證錯誤結構")
  public void testHeader1() throws Exception {
    var expectedText = "";
    var expectedCode = "MD-S-004-00";
    var expectedMessage = "InsufficientAuthenticationException: Full authentication is required to access this resource";
    var expectedSourceId = "sourceId~";
    var expectedTransactionId = "transactionId~";
    mockMvc
        .perform(
            post("/auth")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("sourceId", expectedSourceId)
                .header("transactionId", expectedTransactionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.sourceId", is(equalTo(expectedSourceId))))
        .andExpect(jsonPath("$.transactionId", is(equalTo(expectedTransactionId))))
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.responseId", is(equalTo(expectedResponseId))))
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.details[0].message", is(equalTo(expectedMessage))))
        .andDo(print());
  }

  @Test
  @DisplayName("有送 bearer, 呼叫後端驗證成功 -> 非 strict mode, 強制 pass")
  public void testHeader2() throws Exception {
    when(authClient.validateToken(any())).thenReturn(ValidateResponse.builder()
        .custId("A1231231230")
        .loginIp("192.168.24.1")
        .loginway("MB")
        .fnct("F0201")
        .rtnCode("0000")
        .rtnMsg("OK")
        .jsonObj(Map.of("birthday", "1988-08-08"))
        .build());

    var expectedText = "";
    var expectedUsername = "A1231231230";
    var expectedLoginIp = "192.168.24.1";
    var expectedLoginway = "MB";
    var expectedFnct = "F0201";
    var expectedRtnCode = "0000";
    var expectedRtnMsg = "OK";
    var expectCredentials = "Bearer eyJhbGciOiJIUzUxMiJ9";
    mockMvc
        .perform(
            post("/auth")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("Authorization", expectCredentials))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authenticated", is(true)))
        .andExpect(jsonPath("$.principal.username", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.accountNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.accountNonLocked", is(true)))
        .andExpect(jsonPath("$.principal.credentialsNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.enabled", is(true)))
        .andExpect(jsonPath("$.principal.custId", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.loginIp", is(equalTo(expectedLoginIp))))
        .andExpect(jsonPath("$.principal.loginway", is(equalTo(expectedLoginway))))
        .andExpect(jsonPath("$.principal.fnct", is(equalTo(expectedFnct))))
        .andExpect(jsonPath("$.principal.rtnCode", is(equalTo(expectedRtnCode))))
        .andExpect(jsonPath("$.principal.rtnMsg", is(equalTo(expectedRtnMsg))))
        .andExpect(jsonPath("$.credentials", is(equalTo("N/A"))))
        .andExpect(jsonPath("$.name", is(equalTo(expectedUsername))))
        .andDo(print());
  }

  @Test
  @DisplayName("有送 bearer, 但呼叫後端驗證失敗 -> 非 strict mode, 以匿名登入")
  public void testHeader3() throws Exception {
    when(authClient.validateToken(any())).thenReturn(ValidateResponse.builder()
        .custId("")
        .loginIp("")
        .loginway("MB")
        .fnct("F0201")
        .rtnCode("9999")
        .rtnMsg("Token長度需>3")
        .build());

    var expectedText = "";
    var expectedUsername = "anonymous";
    var expectedRtnCode = "0000";
    var expectCredentials = "Bearer errorAuthorization";
    mockMvc
        .perform(
            post("/auth")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("Authorization", expectCredentials))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authenticated", is(true)))
        .andExpect(jsonPath("$.principal.password", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.username", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.accountNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.accountNonLocked", is(true)))
        .andExpect(jsonPath("$.principal.credentialsNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.enabled", is(true)))
        .andExpect(jsonPath("$.principal.custId", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.loginIp").doesNotExist())
        .andExpect(jsonPath("$.principal.loginway").doesNotExist())
        .andExpect(jsonPath("$.principal.fnct").doesNotExist())
        .andExpect(jsonPath("$.principal.rtnCode", is(equalTo(expectedRtnCode))))
        .andExpect(jsonPath("$.principal.rtnMsg").doesNotExist())
        .andExpect(jsonPath("$.credentials", is(equalTo("N/A"))))
        .andExpect(jsonPath("$.name", is(equalTo(expectedUsername))))
        .andDo(print());
  }

  @Test
  @DisplayName("baggage 已包含json of user-details , 但格式錯誤 -> 非 strict mode, 以匿名登入")
  public void testHeader4() throws Exception {
    var expectedText = "";
    var expectedUsername = "anonymous";
    var expectedRtnCode = "0000";
    var expectedJson = "JsonError";
    mockMvc
        .perform(
            post("/auth")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("Authorization", expectedJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authenticated", is(true)))
        .andExpect(jsonPath("$.principal.password", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.username", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.accountNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.accountNonLocked", is(true)))
        .andExpect(jsonPath("$.principal.credentialsNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.enabled", is(true)))
        .andExpect(jsonPath("$.principal.custId", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.loginIp").doesNotExist())
        .andExpect(jsonPath("$.principal.loginway").doesNotExist())
        .andExpect(jsonPath("$.principal.fnct").doesNotExist())
        .andExpect(jsonPath("$.principal.rtnCode", is(equalTo(expectedRtnCode))))
        .andExpect(jsonPath("$.principal.rtnMsg").doesNotExist())
        .andExpect(jsonPath("$.credentials", is(equalTo("N/A"))))
        .andExpect(jsonPath("$.name", is(equalTo(expectedUsername))))
        .andDo(print());
  }

  @Test
  @DisplayName("baggage 已包含json of user-details , 格式正確 -> 非 strict mode, 強制pass")
  public void testHeader5() throws Exception {
    var expectedText = "";
    var expectedUsername = "A1231231230";
    var expectedLoginIp = "192.168.24.1";
    var expectedLoginway = "MB";
    var expectedFnct = "F0201";
    var expectedRtnCode = "0000";
    var expectedRtnMsg = "OK";
    var expectedJson = "{\"password\":null,\"username\":\"A1231231230\",\"authorities\":[],\"accountNonExpired\":false,\"accountNonLocked\":false,\"credentialsNonExpired\":false,\"enabled\":false,\"custId\":\"A1231231230\",\"loginIp\":\"192.168.24.1\",\"loginway\":\"MB\",\"fnct\":\"F0201\",\"rtnCode\":\"0000\",\"rtnMsg\":\"OK\",\"jsonObj\":{\"traceId\":null}}";

    mockMvc
        .perform(
            post("/auth")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("Authorization", expectedJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.authenticated", is(true)))
        .andExpect(jsonPath("$.principal.username", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.accountNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.accountNonLocked", is(true)))
        .andExpect(jsonPath("$.principal.credentialsNonExpired", is(true)))
        .andExpect(jsonPath("$.principal.enabled", is(true)))
        .andExpect(jsonPath("$.principal.custId", is(equalTo(expectedUsername))))
        .andExpect(jsonPath("$.principal.loginIp", is(equalTo(expectedLoginIp))))
        .andExpect(jsonPath("$.principal.loginway", is(equalTo(expectedLoginway))))
        .andExpect(jsonPath("$.principal.fnct", is(equalTo(expectedFnct))))
        .andExpect(jsonPath("$.principal.rtnCode", is(equalTo(expectedRtnCode))))
        .andExpect(jsonPath("$.principal.rtnMsg", is(equalTo(expectedRtnMsg))))
        .andExpect(jsonPath("$.credentials", is(equalTo("N/A"))))
        .andExpect(jsonPath("$.name", is(equalTo(expectedUsername))))
        .andDo(print());
  }

  @Test
  @DisplayName("測試mapper")
  public void testMapper() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    String s = objectMapper.writeValueAsString(mapper.toSimpleUser(ValidateResponse.builder()
        .custId("A1231231230")
        .loginIp("192.168.24.1")
        .loginway("MB")
        .fnct("F0201")
        .rtnCode("0000")
        .rtnMsg("OK")
        // 以下是為了方便 debug, 把 opentracing 的一些資訊也帶著
        .jsonObj("traceId", TraceContext.getTraceId().orElse(null))
        .build()));
    log.error(s);
  }
}
