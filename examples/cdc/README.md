# Change Data Capture (CDC)
###### 
本範例使用Debezium的kafka connector監視MySQL的變動<br>
發生變動時會將訊息發送至Kafka<br>
在App中監聽到Kafka訊息時可以做對應的行為(本範例是將修改的資料同步寫到另外一張表)

![1](https://debezium.io/documentation/reference/_images/debezium-architecture.png)
> 延伸閱讀: [Debezium ](https://debezium.io/documentation/reference/architecture.html)
---


## 啟動docker
確認電腦裝好docker後執行docker-compose<br>
cd到檔案所在的目錄.\platform\swarm\docker-compose.yaml
```
docker-compose up -d
```

## 設定Debezium connector
設定connector要監視哪個資料庫以及kafka的地址<br>
**程式會自動將./src/main/resources/connector-init/底下的json檔 POST到connector建立監視規則**  

當然!你也可以手動新增監視規則  
範例配置檔[register-mysql.json](./src/main/resources/connector-init/register-mysql.json)
```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d  @register-mysql.json
```
![](https://i.imgur.com/OPznNQm.jpg)

```
查看基本資訊
http://localhost:8083/
目前有多少個connector
http://localhost:8083/connectors/
connector的詳細資訊
http://localhost:8083/connectors/inventory-connector
```

> 詳細參數說明: [Debezium MySQL connector](https://debezium.io/documentation/reference/tutorial.html#:~:text=just%20one%20replica.-,Procedure,-Review%20the%20configuration)


## 啟動App
執行[DebeziumCDCApplication](./src/main/java/tw/com/fcb/mimosa/examples/DebeziumCDCApplication.java)啟動App

>[KafkaConsumer](./src/main/java/tw/com/fcb/mimosa/examples/listener/KafkaConsumer.java):訂閱kafka訊息，將收到的訊息取出CRUD以及[TargetEntity](./src/main/java/tw/com/fcb/mimosa/examples/entity/TargetCustomerEntity.java)<br>最後調用[CustomerService](./src/main/java/tw/com/fcb/mimosa/examples/service/CustomerService.java)做對應的邏輯

## 新增一筆資料
打開類似MySQL Workbench的客戶端在source_customer表插入一筆資料，id是自增主鍵不用給<br>
![](https://i.imgur.com/URM84Vc.jpg)

插入資料後對應的target_customer表也會自動插入相同的資料<br>
![](https://i.imgur.com/AxQ11se.jpg)
