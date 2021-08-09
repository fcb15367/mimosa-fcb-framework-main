package tw.com.fcb.mimosa.unittesting;

import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * @author Matt Ho
 */
@MimosaBootstrap
public class UnitTestingApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(UnitTestingApplication.class, args);
  }
}
