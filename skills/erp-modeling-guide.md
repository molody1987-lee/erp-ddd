---
name: erp-modeling-guide
description: |
  用于在 ERP 系统的 DDD 建模过程中，为新的限界上下文提供从业务识别到代码生成的完整六步法指导。
  适用场景：当需要为采购、销售、库存、生产、财务等子域创建新的聚合根、定义领域事件或设计跨上下文通信时加载此 Skill。
---

# ERP DDD 建模 Skill (MySQL + MyBatis-Plus + RocketMQ 集成版)

## 1. 建模六步法

### Step 1：识别聚合与边界
- **问题**：哪些实体需要保持强一致性？
- **原则**：将需要强一致性的实体放入一个聚合内（如 `PurchaseOrder` 和 `PurchaseOrderItem`）。
- **输出**：聚合根名称、聚合包含的实体列表。

### Step 2：定义值对象
将所有业务量度封装为值对象，提供类型安全：
- `PurchaseOrderId` → 包装 Long（雪花ID）
- `MaterialId` → 包装 Long（引用基础数据）
- `Quantity` → 数量 + 单位
- `Money` → 金额 + 币种

### Step 3：定义仓储接口 (Repository)
- **命名**：`{AggregateRoot}Repository`
- **方法**：只包含 `findById`、`save`（不提供 `findAll` 或 `findByStatus`，避免暴露数据细节）。
- **位置**：`domain/repository/` 包下。

### Step 4：定义领域事件 (Domain Event)
- **事件命名**：过去式，如 `PurchaseOrderReceivedEvent`。
- **事件内容**：包含 `orderId`、`materialId`、`quantity`、`costPrice` 等核心字段。
- **跨上下文事件**：必须定义明确的消费者，并确保消费者实现幂等性。

### Step 5：设计 MySQL 与 MyBatis-Plus 数据映射
**数据库表设计原则**：
1. 表名：`{聚合根名}`，字段名用下划线分隔（如 `order_code`）。
2. PO 类名：`{聚合根名}PO`，使用 `@TableName` 指定表名。
3. 状态字段：使用 `Integer` 存储（对应枚举的 `code` 值）。
4. ID 生成：使用 MyBatis-Plus 的雪花算法 (`@TableId(type = IdType.ASSIGN_ID)`)。

**转换映射**：
- 使用 **MapStruct** 进行 `PO ↔ Domain` 的转换，避免反射性能开销。
- 转换器命名：`{聚合根名}Converter`，如 `PurchaseOrderConverter`。

### Step 6：代码生成顺序
1. 值对象 (Value Object)
2. 聚合根实体 (Aggregate Root)
3. 领域事件 (Domain Event)
4. 仓储接口 (Repository Interface)
5. PO 与 Mapper (Infrastructure)
6. 仓储实现 (Repository Impl)
7. 应用服务 (Application Service)
8. REST Controller (Interface)

## 2. 建模输出模板
参考templates/context代码
