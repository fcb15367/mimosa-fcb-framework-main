package tw.com.fcb.mimosa.examples.loggingjson;

import static java.lang.String.format;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author Matt Ho */
@Slf4j
@RestController
@RequestMapping("/")
public class GreetingController {

  @GetMapping("/greeting/{name}")
  public String greeting(@PathVariable("name") String name) {
    log.info("Hello, {}!", name);
    return format("Hello, %s!", name);
  }
}
