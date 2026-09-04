package com.company.erp.purchase.application.command;

import java.util.List;

/**
 * 采购入库命令。
 */
public record ReceivePurchaseOrderCommand(Long orderId, List<ReceiveItemCommand> items) {
}