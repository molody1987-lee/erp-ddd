package com.company.erp.purchase.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 采购订单持久化对象。
 */
@Data
@TableName("purchase_order")
public class PurchaseOrderPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderCode;
    private Long supplierId;
    private Long orgId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}