package tw.com.fcb.mimosa.dddstructure.remittance.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.NonNull;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceCriteria;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceDto;
import tw.com.fcb.mimosa.http.APIResponse;

/** @author Matt Ho */
@Tag(name = "匯款申請書")
@Validated
@RequestMapping("/remittances")
public interface RemittanceRestApi {

  /**
   * 建立匯款申請書
   *
   * @param request 匯款申請書資訊
   * @return 匯款申請書明細
   */
  @Operation(summary = "建立匯款申請書", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = CreateRemittanceRequest.class))))
  @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class)))
  @PostMapping
  APIResponse<Long> createRemittance(@NotNull @Valid @RequestBody CreateRemittanceRequest request);

  /**
   * 修改匯款申請書
   *
   * @param request 匯款申請書資訊
   * @return 匯款申請書明細
   */
  @Operation(summary = "修改匯款申請書", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReplaceRemittanceRequest.class))))
  @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class)))
  @PutMapping("/{id}")
  APIResponse<Long> replaceRemittance(
      @NonNull @Positive @PathVariable("id") Long id,
      @NotNull @Valid @RequestBody ReplaceRemittanceRequest request);

  /**
   * 根據條件查詢匯款申請書
   *
   * @param criteria 查詢條件
   * @param pageable 分頁條件
   * @return 匯款申請書清單
   */
  @Operation(summary = "根據條件查詢匯款申請書")
  @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RemittanceDto.class)))
  @PageableAsQueryParam
  @GetMapping
  APIResponse<Page<RemittanceDto>> findAll(
      @NotNull @Valid RemittanceCriteria criteria,
      @PageableDefault @Parameter(hidden = true) Pageable pageable);

  /**
   * 依照 ID 查詢匯款申請書
   *
   * @param id 匯款申請書 ID
   * @return 匯款申請書明細
   */
  @Operation(summary = "依照 ID 查詢匯款申請書")
  @ApiResponse(responseCode = "200", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = RemittanceDto.class)))
  @GetMapping("/{id}")
  APIResponse<RemittanceDto> findById(@NonNull @Positive @PathVariable("id") Long id);
}
