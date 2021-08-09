package tw.com.fcb.mimosa.examples.jpa;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * @author Jason Wu
 */
@EnableJpaRepositories
@MimosaBootstrap
public class DataJpaApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(DataJpaApplication.class, args);
  }
}
