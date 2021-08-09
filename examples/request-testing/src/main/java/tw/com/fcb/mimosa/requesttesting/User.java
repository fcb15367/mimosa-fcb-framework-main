package tw.com.fcb.mimosa.requesttesting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static lombok.AccessLevel.PACKAGE;

/**
 * @author Jason Wu
 */
@Data
@Builder
@AllArgsConstructor(access = PACKAGE)
@NoArgsConstructor
@Accessors(chain = true)
public class User {
  String name;
}
