package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.PurchaseDomainException;

/**
 * 入库数量（物料 + 数量）。
 */
public record ReceivedQuantity(MaterialId materialId, Quantity quantity) {

    public ReceivedQuantity {
        if (materialId == null) {
            throw new PurchaseDomainException("入库物料ID不能为空");
        }
        if (quantity == null) {
            throw new PurchaseDomainException("入库数量不能为空");
        }
        if (quantity.value().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new PurchaseDomainException("入库数量必须大于0");
        }
    }
}