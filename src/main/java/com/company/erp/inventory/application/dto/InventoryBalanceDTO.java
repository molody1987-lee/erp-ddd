package com.company.erp.inventory.application.dto;

import java.math.BigDecimal;

/**
 * 库存结存读模型。
 */
public record InventoryBalanceDTO(
        Long id,
        Long materialId,
        Long orgId,
        BigDecimal quantityOnHand,
        String unit,
        BigDecimal unitCost) {
}