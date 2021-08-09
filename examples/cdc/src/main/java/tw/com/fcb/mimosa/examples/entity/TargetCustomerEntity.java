package tw.com.fcb.mimosa.examples.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Jason Wu
 */
@Table("target_customer")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetCustomerEntity {
  @Id
  private Long id;

  @Column("name")
  private String name;

  @Column("email")
  private String email;
}
