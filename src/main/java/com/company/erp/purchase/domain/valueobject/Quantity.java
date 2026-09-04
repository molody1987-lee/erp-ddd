package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 数量值对象（数值 + 单位），不可变。
 */
public record Quantity(BigDecimal value, String unit) {

    public Quantity {
        Objects.requireNonNull(value, "数量不能为空");
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("数量不能为负数");
        }
        if (unit == null || unit.isBlank()) {
            throw new DomainException("数量单位不能为空");
        }
    }

    public static Quantity of(BigDecimal value, String unit) {
        return new Quantity(value, unit);
    }

    public static Quantity zero(String unit) {
        return new Quantity(BigDecimal.ZERO, unit);
    }

    public Quantity add(Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new DomainException("数量单位不一致，无法相加");
        }
        return new Quantity(this.value.add(other.value), this.unit);
    }

    public boolean isPositive() {
        return this.value.compareTo(BigDecimal.ZERO) > 0;
    }
}