package tw.com.fcb.mimosa.examples.openapi;

import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * @author Jason Wu
 */
@MimosaBootstrap
public class OpenapiApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(OpenapiApplication.class, args);
  }
}
