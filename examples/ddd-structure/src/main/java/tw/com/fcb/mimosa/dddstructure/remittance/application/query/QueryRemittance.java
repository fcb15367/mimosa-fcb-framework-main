package tw.com.fcb.mimosa.dddstructure.remittance.application.query;

import static java.lang.String.format;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tw.com.fcb.mimosa.dddstructure.remittance.application.assembler.RemittanceAssembler;
import tw.com.fcb.mimosa.dddstructure.remittance.domain.RemittanceRepository;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceCriteria;
import tw.com.fcb.mimosa.dddstructure.remittance.types.RemittanceDto;

/** @author Matt Ho */
@Service
@RequiredArgsConstructor
public class QueryRemittance {

  final RemittanceRepository repository;
  final RemittanceAssembler assembler;

  public RemittanceDto findById(long id) {
    return repository
        .findById(id)
        .map(assembler::toDto)
        .orElseThrow(
            () -> new IllegalArgumentException(format("Remittance.id [%s] not exist", id)));
  }

  public Page<RemittanceDto> findAll(
      @NotNull @Valid RemittanceCriteria criteria, @NotNull Pageable pageable) {
    return repository.findAll(criteria, pageable).map(assembler::toDto);
  }
}
