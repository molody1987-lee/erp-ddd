package com.company.erp.purchase.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购订单 PO（映射表 purchase_order）。
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
    private BigDecimal totalAmount;
    private String currency;
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Boolean isDeleted;
}