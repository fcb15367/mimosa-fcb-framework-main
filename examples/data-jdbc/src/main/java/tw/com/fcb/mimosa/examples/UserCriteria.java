package tw.com.fcb.mimosa.examples;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/** @author Jason Wu */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCriteria {
  String name;
  Integer age;
  Integer minAge;
  Integer maxAge;
}
