package tw.com.fcb.mimosa.ext.data.jpa;

import java.time.LocalDateTime;

import javax.persistence.Column;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 跟 Audit 有關的系統欄位, 提供配合 {@link Embedded} 使用
 *
 * <p>
 * 要注意的是, 這邊並不包含 ID 及 Version 欄位, 因為在 Spring Data 中有限制這兩個欄位必須要被直些放在 AggregateRoot 中
 *
 * <p>
 * 使用範例:
 *
 * <pre>
 * <code>
 *
 * public class MyAggregate extends AbstractAggregateRoot&lt;MyAggregate&gt; {
 *
 * &#64;Id
 * &#64;Column("ID")
 * private Long id;
 *
 * &#64;Version
 * &#64;Column("VERSION")
 * private Long version;
 *
 * &#64;Embedded.Empty
 * private AuditMetadata<String> audit = new AuditMetadata<>();
 *
 * // … further properties omitted
 *
 * }
 * </code>
 * </pre>
 *
 * @see <a
 *      href=
 *      "https://docs.spring.io/spring-data/jdbc/docs/2.0.9.RELEASE/reference/html/#jdbc.entity-persistence.id-generation">ID
 *      Generation</a>
 * @see <a
 *      href=
 *      "https://docs.spring.io/spring-data/jdbc/docs/2.0.9.RELEASE/reference/html/#jdbc.entity-persistence.optimistic-locking">Optimistic
 *      Locking</a>
 * @see <a
 *      href="https://docs.spring.io/spring-data/commons/docs/current/reference/html/#auditing.annotations">Annotation-based
 *      Auditing Metadata</a>
 * @see AbstractAuditingEntity
 * @author Matt Ho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuditMetadata<U> {

  /** 建立時間 */
  @CreatedDate
  @Column(name = "CREATED_DATE")
  protected LocalDateTime createdDate;

  /** 建立人員 */
  @CreatedBy
  @Column(name = "CREATED_BY")
  protected U createdBy;

  /** 最後修改時間 */
  @LastModifiedDate
  @Column(name = "LAST_MODIFIED_DATE")
  protected LocalDateTime lastModifiedDate;

  /** 最後修改人員 */
  @LastModifiedBy
  @Column(name = "LAST_MODIFIED_BY")
  protected U lastModifiedBy;
}
