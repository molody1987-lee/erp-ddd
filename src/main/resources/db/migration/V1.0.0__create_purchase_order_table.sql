-- 采购订单聚合：采购订单主表 + 采购订单明细表

CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY COMMENT '雪花ID',
    order_code VARCHAR(64) NOT NULL COMMENT '订单编码',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID（引用基础数据上下文）',
    org_id BIGINT NOT NULL COMMENT '组织ID（多组织）',
    status SMALLINT NOT NULL DEFAULT 1 COMMENT '状态：1-草稿 2-已审核 3-部分入库 4-已入库 5-已关闭',
    total_amount DECIMAL(20,4) NOT NULL COMMENT '总金额',
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY' COMMENT '币种',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单表';

CREATE UNIQUE INDEX uk_purchase_order_order_code ON purchase_order(order_code);
CREATE INDEX idx_purchase_order_supplier_id ON purchase_order(supplier_id);
CREATE INDEX idx_purchase_order_org_id ON purchase_order(org_id);
CREATE INDEX idx_purchase_order_status ON purchase_order(status);

CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY COMMENT '雪花ID',
    order_id BIGINT NOT NULL COMMENT '采购订单ID',
    material_id BIGINT NOT NULL COMMENT '物料ID（引用基础数据上下文）',
    quantity DECIMAL(16,4) NOT NULL COMMENT '采购数量',
    unit VARCHAR(16) NOT NULL COMMENT '数量单位',
    received_quantity DECIMAL(16,4) NOT NULL DEFAULT 0 COMMENT '已入库数量',
    unit_price DECIMAL(20,4) NOT NULL COMMENT '单价',
    currency VARCHAR(3) NOT NULL COMMENT '币种',
    create_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采购订单明细表';

CREATE INDEX idx_purchase_order_item_order_id ON purchase_order_item(order_id);
CREATE INDEX idx_purchase_order_item_material_id ON purchase_order_item(material_id);