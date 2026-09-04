package com.company.erp.inventory.application.command;

import java.math.BigDecimal;

/**
 * 采购入库命令，由采购入库事件经 ACL 转换而来。
 */
public record ReceiveInventoryCommand(
        String eventId,
        Long orderId,
        Long materialId,
        Long orgId,
        BigDecimal quantity,
        String unit,
        BigDecimal unitCost,
        String currency) {
}