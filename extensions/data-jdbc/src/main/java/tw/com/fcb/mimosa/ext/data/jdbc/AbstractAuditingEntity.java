package tw.com.fcb.mimosa.ext.data.jdbc;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import tw.com.fcb.mimosa.domain.auditing.AbstractAuditing;
import tw.com.fcb.mimosa.http.validation.groups.OnUpdate;

/**
 * 系統欄位 for Entity
 *
 * @author Matt Ho
 * @see AbstractAuditing
 * @see AuditMetadata
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAuditingEntity<U extends Serializable> implements Serializable {

  /** id */
  @NotNull(groups = OnUpdate.class)
  @Id
  @Column("ID")
  protected Long id;

  /** 建立時間 */
  @CreatedDate
  @Column("CREATED_DATE")
  protected LocalDateTime createdDate;

  /** 建立人員 */
  @CreatedBy
  @Column("CREATED_BY")
  protected U createdBy;

  /** 最後修改時間 */
  @LastModifiedDate
  @Column("LAST_MODIFIED_DATE")
  protected LocalDateTime lastModifiedDate;

  /** 最後修改人員 */
  @LastModifiedBy
  @Column("LAST_MODIFIED_BY")
  protected U lastModifiedBy;

  /** 資料儲存的版本 */
  @Version
  @Column("VERSION")
  protected Long version;
}
