import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import tw.com.fcb.mimosa.ext.cache.MimosaCacheProperties;
import tw.com.fcb.mimosa.ext.cache.support.MimosaCacheManager;
import tw.com.fcb.mimosa.http.APIResponse;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static one.util.streamex.StreamEx.ofKeys;
import static tw.com.fcb.mimosa.http.APIResponse.success;

@Validated
@Tag(name = "Cache", description = "提供直接控制 cache 的 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/caches")
public class MultiCacheController {

  final MimosaCacheManager cacheManager;
  final MimosaCacheProperties cacheProperties;

  @Operation(summary = "Find all cache names")
  @GetMapping
  public APIResponse<Collection<String>> getCacheNames() {
    return success(
        Stream.concat(
            ofKeys(cacheProperties.getRedis().getNames()),
            cacheManager.getCacheNames().stream())
            .distinct()
            .sorted()
            .collect(Collectors.toList()));
  }

  @Operation(summary = "Get the cache value matching the given name and key")
  @GetMapping("/{name:.+}/{key:.+}")
  public APIResponse<?> getCache(
      @PathVariable("name") String name, @PathVariable("key") String key) {
    return cacheManager.get(name, key).map(APIResponse::success).orElseGet(APIResponse::success);
  }

  @Operation(summary = "Delete cache matching the given name")
  @DeleteMapping("/{name:.+}")
  public APIResponse<?> evictCache(@PathVariable("name") String name) {
    cacheManager.evict(name);
    return success();
  }

  @Operation(summary = "Delete cache matching the given name and key")
  @DeleteMapping("/{name:.+}/{key:.+}")
  public APIResponse<?> evictCache(
      @PathVariable("name") String name, @PathVariable("key") String key) {
    cacheManager.evict(name, key);
    return success();
  }

}
