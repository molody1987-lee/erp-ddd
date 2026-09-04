package com.company.erp.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存结存 PO（映射表 inventory_balance）。
 */
@Data
@TableName("inventory_balance")
public class InventoryBalancePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long materialId;
    private Long orgId;
    private String unit;
    private String currency;
    private BigDecimal quantityOnHand;
    private BigDecimal totalCost;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}