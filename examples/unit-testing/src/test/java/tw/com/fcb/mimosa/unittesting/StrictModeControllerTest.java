package tw.com.fcb.mimosa.unittesting;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tw.com.fcb.mimosa.test.MimosaTest;
import tw.com.fcb.mimosa.test.TestProfile;
import tw.com.fcb.mimosa.test.util.GlobalTracerTestUtil;

@Slf4j
@TestProfile
@MimosaTest
@Timeout(1)
@AutoConfigureMockMvc
class StrictModeControllerTest {

  @Autowired
  MockMvc mockMvc;
  String expectedResponseId = "test";

  @BeforeAll
  @AfterAll
  static void reset() {
    GlobalTracerTestUtil.resetGlobalTracer();
  }

  @Test
  @DisplayName("API 呼叫時沒有提供sourceId")
  public void testHeader1() throws Exception {
    var expectedText = "Hello World!";
    var expectedTransactionId = "transactionId~";
    var expectedCode = "MD-S-500-00";
    var expectedMessage = "IllegalArgumentException: Request header 'sourceId' is required in strict mode";
    mockMvc
        .perform(
            post("/api/response/header")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("transactionId", expectedTransactionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.sourceId").doesNotExist())
        .andExpect(jsonPath("$.transactionId", is(equalTo(expectedTransactionId))))
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.responseId", is(equalTo(expectedResponseId))))
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.message", is(equalTo(expectedMessage))))
        .andDo(print());
  }

  @Test
  @DisplayName("API 呼叫時沒有提供transactionId")
  public void testHeader2() throws Exception {
    var expectedText = "Hello World!";
    var expectedSourceId = "sourceId~";
    mockMvc
        .perform(
            post("/api/response/header")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("sourceId", expectedSourceId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.transactionId").exists())
        .andExpect(jsonPath("$.sourceId", is(equalTo(expectedSourceId))))
        .andExpect(jsonPath("$.clientResponse", is(equalTo(expectedText))))
        .andExpect(jsonPath("$.responseId", is(equalTo(expectedResponseId))))
        .andDo(print());
  }

  @Test
  @DisplayName("API 呼叫時沒有提供 sourceId & transactionId")
  public void testHeader3() throws Exception {
    var expectedText = "Hello World!";
    var expectedCode = "MD-S-500-00";
    var expectedMessage = "IllegalArgumentException: Request header 'sourceId' is required in strict mode";
    mockMvc
        .perform(
            post("/api/response/header")
                .content(expectedText)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.sourceId").doesNotExist())
        .andExpect(jsonPath("$.transactionId").exists())
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.responseId", is(equalTo(expectedResponseId))))
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.message", is(equalTo(expectedMessage))))
        .andDo(print());
  }

  @Test
  @DisplayName("API 呼叫時有提供 sourceId & transactionId")
  public void testHeader4() throws Exception {
    var expectedText = "Hello World!";
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
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.error").doesNotExist())
        .andExpect(jsonPath("$.sourceId", is(equalTo(expectedSourceId))))
        .andExpect(jsonPath("$.transactionId", is(equalTo(expectedTransactionId))))
        .andExpect(jsonPath("$.clientResponse", is(equalTo(expectedText))))
        .andExpect(jsonPath("$.responseId", is(equalTo(expectedResponseId))))
        .andDo(print());
  }

  @Test
  @DisplayName("測試Throw Error")
  public void testThrowError() throws Exception {
    var expectedCode = "SOME_ERROR_CODE";
    var expectedMessage = "Should not reach here!!";
    var expectedSourceId = "sourceId~";
    mockMvc
        .perform(
            post("/api/response/throw-error")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("sourceId", expectedSourceId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.message", is(equalTo(expectedMessage))))
        .andDo(print());
  }

  @Test
  @DisplayName("測試APIResponse Error")
  public void testAPIResponseError() throws Exception {
    var expectedCode = "SOME_ERROR_CODE";
    var expectedMessage = "Should not reach here!!";
    mockMvc
        .perform(
            post("/api/response/apiresponse-error")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("sourceId", "sourceId~"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.message", is(equalTo(expectedMessage))))
        .andDo(print());
  }

  @Test
  @DisplayName("測試RuntimeException")
  public void testRuntimeException() throws Exception {
    var expectedCode = "MD-S-500-00";
    var expectedMessage = "RuntimeException: ";
    mockMvc
        .perform(
            post("/api/response/runtime-exception")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .header("sourceId", "sourceId~"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.clientResponse").doesNotExist())
        .andExpect(jsonPath("$.error.code", is(equalTo(expectedCode))))
        .andExpect(jsonPath("$.error.message", is(equalTo(expectedMessage))))
        .andDo(print());
  }

}
