package com.company.erp.inventory.domain.valueobject;

import com.company.erp.inventory.domain.exception.InventoryDomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 库存数量值对象（数值 + 单位），不可变。
 */
public record InventoryQuantity(BigDecimal value, String unit) {

    public InventoryQuantity {
        Objects.requireNonNull(value, "数量不能为空");
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InventoryDomainException("数量必须为正数");
        }
        if (unit == null || unit.isBlank()) {
            throw new InventoryDomainException("数量单位不能为空");
        }
    }

    public static InventoryQuantity of(BigDecimal value, String unit) {
        return new InventoryQuantity(value, unit);
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }
}