/**
 * rest 中定義 Exposed API (with Swagger annotation) and DTO (req, resp object)
 *
 * <p>
 * 物件 Naming:
 *
 * <ul>
 * <li>API: {Aggregate}RestApi
 * <li>Req: {Command}Request
 * <li>Resp: {Command}Response or {Aggregate}Response
 * <li>Dto: 當某 API 中 Req 跟 Resp 物件一樣時, 可使用 {Aggregate}Dto 代替
 * <li>Criteria: {Aggregate}Criteria 查詢資源的條件
 * </ul>
 *
 * <p>
 * CRUD 方法 Naming:
 *
 * <ul>
 * <li>新增資源: create{Aggregate}
 * <li>覆蓋資源: replace{Aggregate} (覆蓋傳入的所有欄位, 只要過了 JSR303 驗證就會直接 copy 到 db 中, 包含了 null 欄位等)
 * <li>更新部分資源: update{Aggregate} (更新部分資源通常會做欄位的邏輯比對, 例如有傳才更新)
 * <li>刪除資源: delete{Aggregate}
 * <li>查詢全部: findAll (包含了查詢條件, 並只能回傳 Page 物件)
 * <li>依照 ID 查詢單筆: findById (ID 必須是 Aggregate ID)
 * <li>依照 Unique 條件查詢單筆: findOne
 * </ul>
 *
 * @author Matt Ho
 */
package tw.com.fcb.mimosa.dddstructure.remittance.rest;
