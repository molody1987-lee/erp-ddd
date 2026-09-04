package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.PurchaseDomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 数量值对象（数量 + 单位，不可变）。
 */
public record Quantity(BigDecimal value, String unit) {

    public Quantity {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new PurchaseDomainException("数量不能为空且必须非负");
        }
        if (unit == null || unit.isBlank()) {
            throw new PurchaseDomainException("数量单位不能为空");
        }
    }

    /**
     * 同单位数量相加。
     */
    public Quantity add(Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new PurchaseDomainException("单位不一致，无法相加");
        }
        return new Quantity(this.value.add(other.value), this.unit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Quantity that)) {
            return false;
        }
        return Objects.equals(value, that.value) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
}