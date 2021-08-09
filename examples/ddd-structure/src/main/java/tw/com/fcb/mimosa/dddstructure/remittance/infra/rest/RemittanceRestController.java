package tw.com.fcb.mimosa.dddstructure.remittance.infra.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import tw.com.fcb.mimosa.dddstructure.remittance.application.command.CreateRemittanceCommand;
import tw.com.fcb.mimosa.dddstructure.remittance.application.command.ReplaceRemittanceCommand;
import tw.com.fcb.mimosa.dddstructure.remittance.application.query.QueryRemittance;
import tw.com.fcb.mimosa.dddstructure.remittance.rest.CreateRemittanceRequest;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceCriteria;
import tw.com.fcb.mimosa.dddstructure.remittance.rest.RemittanceRestApi;
import tw.com.fcb.mimosa.dddstructure.remittance.rest.ReplaceRemittanceRequest;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceDto;
import tw.com.fcb.mimosa.ext.ddd.application.UseCases;
import tw.com.fcb.mimosa.http.APIResponse;

/** @author Matt Ho */
@RestController
@RequiredArgsConstructor
class RemittanceRestController implements RemittanceRestApi {

  final UseCases useCases;
  final QueryRemittance queryRemittance;

  @Override
  public APIResponse<Long> createRemittance(CreateRemittanceRequest request) {
    return APIResponse.success(
        useCases.execute(
            CreateRemittanceCommand.builder()
                .bank(request.getBank())
                .money(request.getMoney())
                .build()));
  }

  @Override
  public APIResponse<Long> replaceRemittance(Long id, ReplaceRemittanceRequest request) {
    return APIResponse.success(
        useCases.execute(
            ReplaceRemittanceCommand.builder().id(id).money(request.getMoney()).build()));
  }

  @Override
  public APIResponse<Page<RemittanceDto>> findAll(RemittanceCriteria criteria, Pageable pageable) {
    return APIResponse.success(queryRemittance.findAll(criteria, pageable));
  }

  @Override
  public APIResponse<RemittanceDto> findById(Long id) {
    return APIResponse.success(queryRemittance.findById(id));
  }
}
