CREATE TABLE inventory_balance
(
    id               BIGINT        NOT NULL COMMENT '主键（雪花ID）',
    material_id      BIGINT        NOT NULL COMMENT '物料ID',
    org_id           BIGINT        NOT NULL COMMENT '组织ID',
    quantity_on_hand DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '现存量',
    unit             VARCHAR(16)   NOT NULL COMMENT '单位',
    unit_cost        DECIMAL(20,4) NOT NULL DEFAULT 0 COMMENT '移动加权平均成本',
    create_time      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time      DATETIME(3)   NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory_balance_material_org (material_id, org_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='库存结存';