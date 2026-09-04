package com.company.erp.purchase.application.command;

import com.company.erp.purchase.domain.valueobject.PurchaseOrderId;

/**
 * 审核采购订单命令。
 */
public record ReviewPurchaseOrderCommand(PurchaseOrderId orderId, Long reviewerId) {
}