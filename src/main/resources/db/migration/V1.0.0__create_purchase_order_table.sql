CREATE TABLE purchase_order
(
    id          BIGINT       NOT NULL COMMENT '主键（雪花ID）',
    order_code  VARCHAR(64)  NOT NULL COMMENT '订单编号',
    supplier_id BIGINT       NOT NULL COMMENT '供应商ID',
    org_id      BIGINT       NOT NULL COMMENT '组织ID',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0草稿 1已审核 2已入库 3已关闭',
    create_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_purchase_order_code (order_code),
    KEY idx_purchase_order_status (status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='采购订单';

CREATE TABLE purchase_order_item
(
    id                BIGINT        NOT NULL COMMENT '主键（雪花ID）',
    order_id          BIGINT        NOT NULL COMMENT '采购订单ID',
    material_id       BIGINT        NOT NULL COMMENT '物料ID',
    quantity          DECIMAL(20,4) NOT NULL COMMENT '采购数量',
    unit              VARCHAR(16)   NOT NULL COMMENT '单位',
    unit_price        DECIMAL(20,4) NOT NULL COMMENT '含税单价',
    currency          VARCHAR(8)    NOT NULL COMMENT '币种',
    received_quantity DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '已入库数量',
    create_time       DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time       DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_purchase_order_item_order (order_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='采购订单明细';