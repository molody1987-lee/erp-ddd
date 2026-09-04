---
name: erp-cross-context-events
description: |
  ERP 系统跨上下文领域事件的完整清单和规范定义。
  适用场景：当需要设计或实现跨上下文的异步通信（采购→库存、库存→财务、销售→库存等），
  或需要定义新的事件 Schema、检查事件发布/消费的合规性时加载此 Skill。
---

# ERP 跨上下文事件清单

## 1. 事件 Schema 模板

```json
{
  "eventId": "UUID",
  "eventType": "string",
  "sourceContext": "string",
  "occurredOn": "ISO-8601 timestamp",
  "payload": {
    "orderId": "string",
    "materialId": "string",
    "quantity": 100,
    "unitOfMeasure": "PCS",
    "costPrice": 15.50,
    "currency": "CNY"
  },
  "tenantId": "string",
  "correlationId": "string"
}

##2. 事件定义表
事件名称	发布者	消费者	Payload 关键字段	触发条件
PurchaseOrderReceivedEvent	采购	库存	poId, items[{materialId, quantity, cost}]	采购入库单确认
SalesOrderConfirmedEvent	销售	库存	soId, items[{materialId, quantity}]	销售订单确认（备货/出库）
SalesOrderShippedEvent	销售	库存	soId, shipmentId, items	销售出库单确认
InventoryTransactionCreatedEvent	库存	财务	txnId, materialId, quantity, amount, moveType	任何库存移动（入库/出库/盘点）
WorkOrderCompletedEvent	生产	库存	woId, productId, quantity	生产工单完工报产
WorkOrderMaterialIssueEvent	生产	库存	woId, items[{materialId, quantity}]	生产工单领料

##3.事件版本管理规则
每个事件有 version 字段（整数，从 1 开始）。
新增字段时版本号 +1，并确保向下兼容（新增字段设为 optional）。
禁止：删除或重命名已有字段（会破坏消费者）。
建议：使用 Avro/Protobuf 作为序列化方案，避免 JSON 的 Schema 漂移。

##4. 事件发布确认流程
sequenceDiagram
    participant Publisher as 采购上下文
    participant MQ as RocketMQ
    participant Consumer as 库存上下文

    Publisher->>MQ: 发布 PurchaseOrderReceivedEvent
    MQ-->>Publisher: ACK (确认收到)
    MQ->>Consumer: 推送事件
    Consumer->>Consumer: 处理事件（扣减/增加库存）
    Consumer-->>MQ: COMMIT (确认消费成功)
    alt 处理失败
        Consumer-->>MQ: 触发重试 (最多3次)
        MQ-->>Consumer: 重试失败 -> 进入死信队列 (DLQ)
    end

##5. RocketMQ 集成检查清单
□ 事件类是否实现了序列化接口？
□ 事件发送是否在 afterCommit 中执行？
□ 消费者是否使用 @RocketMQMessageListener 注解？
□ 消费者是否实现了幂等性（基于 correlationId 去重）？
□ 是否配置了重试次数和 DLQ（死信队列）？
