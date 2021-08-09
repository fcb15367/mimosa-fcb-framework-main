package tw.com.fcb.mimosa.examples;

import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

/**
 * @author Jason Wu
 */
@EnableJdbcRepositories
@MimosaBootstrap
public class DataJdbcApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(DataJdbcApplication.class, args);
  }

}
