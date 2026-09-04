package com.company.erp.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存结存持久化对象。
 */
@Data
@TableName("inventory_balance")
public class InventoryBalancePO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long materialId;
    private Long orgId;
    private BigDecimal quantityOnHand;
    private String unit;
    private BigDecimal unitCost;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}