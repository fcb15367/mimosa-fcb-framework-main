package tw.com.fcb.mimosa.examples.security;

import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * @author Jason Wu
 */
@MimosaBootstrap
public class SecurityApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(SecurityApplication.class, args);
  }
}
