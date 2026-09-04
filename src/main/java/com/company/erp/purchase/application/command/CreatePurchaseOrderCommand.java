package com.company.erp.purchase.application.command;

import java.util.List;

/**
 * 创建采购订单命令。
 */
public record CreatePurchaseOrderCommand(
        String orderCode,
        Long supplierId,
        Long orgId,
        List<CreatePurchaseOrderItemCommand> items) {
}