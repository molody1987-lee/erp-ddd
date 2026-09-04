
---

## 🧩 Skill 3：ERP 成本计算规范

```yaml
---
name: cost-calculation-guide
description: |
  ERP 系统的库存成本计算规范，采用移动加权平均法。
  适用场景：当需要实现采购入库、生产完工入库、销售出库、盘点等场景的成本计算逻辑时加载此 Skill。
---

# ERP 成本计算规范（移动加权平均法）

## 1. 适用场景

| 业务事件 | 成本计算动作 |
| :--- | :--- |
| 采购入库 (`PurchaseOrderReceivedEvent`) | 重新计算加权平均成本 |
| 生产完工入库 (`WorkOrderCompletedEvent`) | 重新计算加权平均成本 |
| 销售出库 (`SalesOrderShippedEvent`) | 使用当前加权平均价作为出库成本 |
| 盘点盘盈 (`InventoryCountAdjustmentEvent`) | 重新计算加权平均成本 |
| 盘点盘亏 (`InventoryCountAdjustmentEvent`) | 使用当前加权平均价作为出库成本 |

## 2. 移动加权平均法公式
新加权平均成本 = (原库存总金额 + 新入库金额) / (原库存总数量 + 新入库数量)
**约束条件**：
- 仅**入库**时重新计算成本（采购入库、生产完工入库、盘盈）。
- 出库时使用当前加权平均成本作为出库成本单价。

## 3. 成本计算聚合示例
参考templates/aggregate_root.md

##4. 错误处理与并发控制
-如果成本计算结果为负（理论上不可能），记录告警日志并触发人工介入。
-如果成本计算涉及高并发入库，使用 Redis 分布式锁保证原子性。
-锁名规范：lock:inventory:material_{materialId}
-锁超时时间：5 秒

##5. 成本报表接口
参考templates/cost_report_interface.md

##6.成本计算检查清单
□ 入库事件是否触发了加权平均成本重新计算？
□ 出库事件是否使用了当前加权平均价？
□ 高并发场景是否使用了 Redis 分布式锁？
□ 成本计算结果是否有日志记录（便于审计）？
