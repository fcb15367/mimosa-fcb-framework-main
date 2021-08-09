package tw.com.fcb.mimosa.domain.auditing;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 系統欄位 for DTO
 *
 * <p>
 * 這個通常 for 轉出 DTO 用, Entity 或 Embedded 要用請參考:
 *
 * <ul>
 * <li>Entity: {@code tw.com.fcb.mimosa.ext.data.jdbc.AbstractAuditingEntity}
 * <li>Embedded: {@code tw.com.fcb.mimosa.ext.data.jdbc.AuditMetadata}
 * </ul>
 *
 * @author Matt Ho
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public abstract class AbstractAuditing<U extends Serializable> implements Serializable {

  /** id */
  protected Long id;

  /** 建立時間 */
  protected LocalDateTime createdDate;

  /** 建立人員 */
  protected U createdBy;

  /** 最後修改時間 */
  protected LocalDateTime lastModifiedDate;

  /** 最後修改人員 */
  protected U lastModifiedBy;

  /** 資料儲存的版本 */
  protected Long version;
}
