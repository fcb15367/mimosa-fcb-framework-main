package tw.com.fcb.mimosa.examples.openfeign;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Tag(name = "OpenFeign", description = "測試")
@RequiredArgsConstructor
public class OpenFeignController {

  final SoftLeaderIndexClient softLeaderIndexClient;

  @Operation(summary = "連到松凌官網")
  @GetMapping
  public String getIndex() {
    return softLeaderIndexClient.getIndex();
  }

}
