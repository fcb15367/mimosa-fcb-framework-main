package tw.com.fcb.mimosa.examples.security;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "OpenFeign", description = "測試")
@RequiredArgsConstructor
public class SecurityController {

  @PostMapping
  public Authentication currentAuthentication(Authentication authentication) {
    return authentication;
  }

}
