package tw.com.fcb.mimosa.dddstructure;

import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import tw.com.fcb.mimosa.Mimosa;
import tw.com.fcb.mimosa.MimosaBootstrap;

@EnableJdbcRepositories
@MimosaBootstrap
public class DddStructureApplication {

  public static void main(String[] args) {
    Mimosa.bootstrap(DddStructureApplication.class, args);
  }
}
