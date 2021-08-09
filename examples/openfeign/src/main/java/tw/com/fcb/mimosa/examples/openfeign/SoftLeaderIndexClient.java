package tw.com.fcb.mimosa.examples.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "SoftLeaderIndexClient", url = "https://www.softleader.com.tw")
public interface SoftLeaderIndexClient {
  @RequestMapping("/index.html")
  String getIndex();
}
