
---

## 🧩 Skill 5：ERP MySQL 表设计规范

```yaml
---
name: erp-mysql-schema-guide
description: |
  ERP 系统的 MySQL 数据库表设计规范，涵盖表命名、字段类型、索引命名、字符集等标准。
  适用场景：当需要创建新的数据库表、设计 PO 类、或编写 Flyway 迁移脚本时加载此 Skill。
---

# ERP MySQL 表设计规范

## 1. 核心规范

| 规范项 | 约定 | 示例 |
| :--- | :--- | :--- |
| **表名** | 全小写，下划线分隔（snake_case） | `purchase_order`, `sales_order_item` |
| **主键** | `id BIGINT`，MyBatis-Plus 雪花算法生成 | `@TableId(type = IdType.ASSIGN_ID)` |
| **创建时间** | `create_time DATETIME(3)`，自动填充 | `@TableField(fill = FieldFill.INSERT)` |
| **更新时间** | `update_time DATETIME(3)`，自动填充 | `@TableField(fill = FieldFill.INSERT_UPDATE)` |
| **状态字段** | `status SMALLINT` 或 `TINYINT` | 对应枚举的 `code` 值 |
| **金额字段** | `DECIMAL(20,4)` | 保证精度，避免浮点误差 |
| **数量字段** | `DECIMAL(16,4)` | 支持小数单位（如 kg） |
| **字符集** | `utf8mb4` | 排序规则 `utf8mb4_unicode_ci` |
| **主键索引** | `PRIMARY KEY (id)` | 自动创建 |
| **普通索引** | `idx_{表名}_{字段名}` | `idx_purchase_order_status` |
| **唯一索引** | `uk_{表名}_{字段名}` | `uk_purchase_order_order_code` |

## 2. DDL 模板

```sql
CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY COMMENT '雪花ID',
    order_code VARCHAR(64) NOT NULL COMMENT '订单编码',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态：1-草稿 2-已审核 3-部分入库 4-已入库 5-已关闭',
    total_amount DECIMAL(20,4) NOT NULL COMMENT '总金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单表';

CREATE UNIQUE INDEX uk_purchase_order_order_code ON purchase_order(order_code);
CREATE INDEX idx_purchase_order_supplier_id ON purchase_order(supplier_id);
CREATE INDEX idx_purchase_order_status ON purchase_order(status);

## 3. PO 类模板
参考templates/po.md
## 4. Flyway 迁移规范
-**文件命名**：V{版本号}__{描述}.sql，如 V1.0.0__create_purchase_order_table.sql
-**版本号**：主版本.次版本.补丁版本，如 1.0.0
-**禁止**：在已上线的迁移脚本中修改已执行的 SQL（应创建新的迁移文件）

## 5. 建表检查清单
□ 表名是否使用小写 + 下划线？
□ 是否有 id 作为雪花算法主键？
□ 是否有 create_time 和 update_time 自动填充字段？
□ 金额字段是否使用 DECIMAL 而不是 FLOAT/DOUBLE？
□ 字符集是否设置为 utf8mb4？
□ 索引命名是否遵循规范？
