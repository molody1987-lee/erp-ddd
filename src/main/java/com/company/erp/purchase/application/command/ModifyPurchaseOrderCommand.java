package com.company.erp.purchase.application.command;

import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;

import java.util.List;

/**
 * 修改采购订单命令。
 */
public record ModifyPurchaseOrderCommand(
        PurchaseOrderId orderId,
        String orderCode,
        Long supplierId,
        List<CreatePurchaseOrderItemCommand> items) {
}