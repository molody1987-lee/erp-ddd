package com.company.erp.purchase.application.command;

import java.math.BigDecimal;

/**
 * 采购订单明细行命令。
 */
public record ItemCommand(
        Long materialId,
        BigDecimal quantity,
        String unit,
        BigDecimal unitPrice) {
}