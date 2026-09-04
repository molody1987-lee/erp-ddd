package com.company.erp.inventory.application.command;

import java.math.BigDecimal;

/**
 * 导入库存结存命令（期初余额）。
 */
public record ImportInventoryBalanceCommand(
        Long materialId,
        Long orgId,
        String unit,
        BigDecimal quantity,
        BigDecimal unitCost) {
}