package com.company.erp.inventory.application.acl;

import com.company.erp.inventory.domain.valueobject.InventoryQuantity;

import java.math.BigDecimal;

/**
 * 库存入库条目（ACL 生成的内部领域友好对象）。
 */
public record InventoryReceiveItem(Long materialId, InventoryQuantity quantity, BigDecimal unitCost) {
}