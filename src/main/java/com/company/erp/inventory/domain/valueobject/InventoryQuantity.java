package com.company.erp.inventory.domain.valueobject;

import com.company.erp.inventory.domain.exception.InventoryDomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 库存数量值对象（数量 + 单位，不可变）。
 */
public record InventoryQuantity(BigDecimal value, String unit) {

    public InventoryQuantity {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InventoryDomainException("库存数量不能为空且不能为负");
        }
        if (unit == null || unit.isBlank()) {
            throw new InventoryDomainException("库存单位不能为空");
        }
    }

    public static InventoryQuantity of(BigDecimal value, String unit) {
        return new InventoryQuantity(value, unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryQuantity that)) {
            return false;
        }
        return Objects.equals(value, that.value) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
}