package tw.com.fcb.mimosa.examples.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * @author Jason Wu
 */
@MimosaBootstrap
@EnableFeignClients
public class OpenFeignApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(OpenFeignApplication.class, args);
  }
}
