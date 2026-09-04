package com.company.erp.purchase.application.command;

/**
 * 审核采购订单命令。
 */
public record ReviewPurchaseOrderCommand(Long orderId, Long operatorId) {
}