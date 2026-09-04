package com.company.erp.purchase.application.command;

import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;
import com.company.erp.purchase.domain.valueobject.ReceivedQuantity;

import java.util.List;

/**
 * 采购入库命令。
 */
public record ReceivePurchaseOrderCommand(
        PurchaseOrderId orderId,
        List<ReceivedQuantity> receivedItems) {
}