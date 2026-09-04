package com.company.erp.purchase.application.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 采购订单明细读模型。
 */
@Data
public class PurchaseOrderItemDTO {

    private Long id;
    private Long materialId;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal receivedQuantity;
    private BigDecimal unitPrice;
    private String currency;
}