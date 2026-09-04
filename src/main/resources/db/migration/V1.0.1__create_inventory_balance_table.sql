-- 库存结存表（移动加权平均成本）

CREATE TABLE inventory_balance (
    id BIGINT PRIMARY KEY COMMENT '雪花ID',
    material_id BIGINT NOT NULL COMMENT '物料ID（引用基础数据上下文）',
    org_id BIGINT NOT NULL COMMENT '组织ID（多组织）',
    unit VARCHAR(16) NOT NULL COMMENT '数量单位',
    currency VARCHAR(3) NOT NULL COMMENT '币种',
    quantity_on_hand DECIMAL(16,4) NOT NULL DEFAULT 0 COMMENT '结存数量',
    total_cost DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '结存总成本（数量×加权平均成本累计）',
    create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存结存表';

CREATE UNIQUE INDEX uk_inventory_balance_material_org ON inventory_balance(material_id, org_id);