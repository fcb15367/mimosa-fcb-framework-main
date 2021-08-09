package tw.com.fcb.mimosa.ext.data.jpa;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

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
  @Column(name = "ID")
  protected Long id;

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

  /** 資料儲存的版本 */
  @Version
  @Column(name = "VERSION")
  protected Long version;
}
