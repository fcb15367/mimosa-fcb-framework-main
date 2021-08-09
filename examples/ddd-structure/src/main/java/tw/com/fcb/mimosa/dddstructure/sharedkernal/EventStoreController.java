package tw.com.fcb.mimosa.dddstructure.sharedkernal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.fcb.mimosa.dddstructure.sharedkernal.repository.EventStoreRepositoryImpl;
import tw.com.fcb.mimosa.dddstructure.sharedkernal.service.Event;

/**
 * 這隻程式 only for example demo 使用, 一般 App 開發不建議有 select events 的 api
 *
 * @author Matt Ho
 */
@Tag(name = "Only for example demo")
@RestController
@RequestMapping("/demo/events")
@RequiredArgsConstructor
public class EventStoreController {

  final EventStoreRepositoryImpl repository;

  @Operation(summary = "Get all events")
  @GetMapping
  public Iterable<Event> getAllEvents() {
    return repository.findAll();
  }
}
