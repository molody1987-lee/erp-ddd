package com.company.erp.purchase.application.command;

/**
 * 关闭采购订单命令。
 */
public record ClosePurchaseOrderCommand(Long orderId, Long operatorId, String reason) {
}