package com.company.erp.purchase.application.command;

import java.math.BigDecimal;

/**
 * 创建采购订单明细命令。
 */
public record CreatePurchaseOrderItemCommand(
        Long materialId,
        BigDecimal quantity,
        String unit,
        BigDecimal unitPrice,
        String currency) {
}