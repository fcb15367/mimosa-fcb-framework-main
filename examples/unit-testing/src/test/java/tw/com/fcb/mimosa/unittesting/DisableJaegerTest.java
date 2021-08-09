package tw.com.fcb.mimosa.unittesting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tw.com.fcb.mimosa.test.MimosaTest;
import tw.com.fcb.mimosa.test.TestProfile;
import tw.com.fcb.mimosa.test.util.GlobalTracerTestUtil;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestProfile
@ActiveProfiles({ "disable-jaeger" })
@MimosaTest
@Timeout(1)
@AutoConfigureMockMvc
public class DisableJaegerTest {

  @BeforeAll
  @AfterAll
  static void reset() {
    GlobalTracerTestUtil.resetGlobalTracer();
  }

  @Autowired
  MockMvc mockMvc;

  String expectedResponseId = "test";

  @Test
  @DisplayName("測試jaeger關閉時的異常")
  public void testHeader() throws Exception {
    var expectedText = "Hello World!";
    var expectedCode = "MD-S-500-00";
    var expectedMessage = "IllegalStateException: Neither 'transactionId' from header nor TraceId from OpenTracing are found, consider adding 'opentracing.jaeger.enabled=true' in your properties.";
    var expectedSourceId = "sourceId~";
    var expectedTransactionId = "transactionId~";
    mockMvc
        .perform(
            post("/api/response/header")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("sourceId", expectedSourceId)
                .header("transactionId", expectedTransactionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.message", is(equalTo(expectedMessage))))
        .andExpect(jsonPath("$.sourceId").doesNotExist())
        .andExpect(jsonPath("$.transactionId").doesNotExist())
        .andExpect(jsonPath("$.responseId", is(equalTo(expectedResponseId))))
        .andDo(print());
  }
}
