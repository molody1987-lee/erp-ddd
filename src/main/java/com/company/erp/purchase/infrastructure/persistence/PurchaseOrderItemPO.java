package com.company.erp.purchase.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购订单明细持久化对象。
 */
@Data
@TableName("purchase_order_item")
public class PurchaseOrderItemPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long orderId;
    private Long materialId;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitPrice;
    private String currency;
    private BigDecimal receivedQuantity;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}