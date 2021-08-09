package tw.com.fcb.mimosa.examples;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/** @author Jason Wu */
@Table("USER")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity {
  @Id
  @Column("ID")
  private Long id;

  @Column("NAME")
  @NotBlank
  private String name;

  @Column("AGE")
  @NotNull
  @Min(1)
  private Integer age;
}
