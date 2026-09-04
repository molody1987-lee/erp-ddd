package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.PurchaseDomainException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 金额值对象（金额 + 币种，不可变）。
 */
public record Money(BigDecimal amount, String currency) {

    public Money {
        if (amount == null) {
            throw new PurchaseDomainException("金额不能为空");
        }
        if (currency == null || currency.isBlank()) {
            throw new PurchaseDomainException("币种不能为空");
        }
    }

    /**
     * 金额乘以数量得到总金额。
     */
    public Money multiply(Quantity quantity) {
        return new Money(this.amount.multiply(quantity.value()), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money that)) {
            return false;
        }
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}