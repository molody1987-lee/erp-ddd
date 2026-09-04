package com.company.erp.purchase.application.command;

import java.util.List;

/**
 * 创建采购订单命令。
 */
public record CreatePurchaseOrderCommand(
        Long supplierId,
        Long orgId,
        String currency,
        String remark,
        Long createdBy,
        List<ItemCommand> items) {
}