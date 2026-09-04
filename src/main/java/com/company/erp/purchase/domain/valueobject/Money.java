package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 金额值对象（金额 + 币种），不可变。
 */
public record Money(BigDecimal amount, String currency) {

    public Money {
        Objects.requireNonNull(amount, "金额不能为空");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("金额不能为负数");
        }
        if (currency == null || currency.isBlank()) {
            throw new DomainException("币种不能为空");
        }
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new DomainException("币种不一致，无法相加");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }
}