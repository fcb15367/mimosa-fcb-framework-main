package tw.com.fcb.mimosa.http;

import static lombok.AccessLevel.PACKAGE;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import tw.com.fcb.mimosa.domain.t9n.Term;

/** @author Matt Ho */
@Data
@SuperBuilder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
public class APIErrorT9n {

  @NonNull
  Term term;

  /** 錯誤明細 */
  @ArraySchema
  @Singular
  List<? extends APIErrorDetail> details;
}
