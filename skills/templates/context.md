上下文名称：采购上下文
聚合根：PurchaseOrder
聚合根 ID：PurchaseOrderId
实体：PurchaseOrderItem (多对一，依赖父聚合)
值对象：

PurchaseOrderStatus (草稿、已审核、部分入库、已入库、已关闭)

Quantity (数量 + 单位)

UnitPrice (单价 + 币种)

SupplierId (引用基础数据上下文)

OrganizationId (多组织)
领域事件：

PurchaseOrderCreatedEvent

PurchaseOrderReviewedEvent

PurchaseOrderReceivedEvent (触发库存入库)

PurchaseOrderClosedEvent
仓储：PurchaseOrderRepository (findById, save)
领域服务：PurchaseOrderValidator (验证数量是否为正、物料是否有效)