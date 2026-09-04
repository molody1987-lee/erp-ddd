package com.company.erp.purchase.application.dto;

import java.math.BigDecimal;

/**
 * 采购订单明细读模型。
 */
public record PurchaseOrderItemDTO(
        Long id,
        Long materialId,
        BigDecimal quantity,
        String unit,
        BigDecimal unitPrice,
        String currency,
        BigDecimal receivedQuantity) {
}