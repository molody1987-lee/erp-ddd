package com.company.erp.purchase.domain.valueobject;

import java.util.Objects;

/**
 * 采购订单ID值对象。
 */
public record PurchaseOrderId(Long value) {

    public PurchaseOrderId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("采购订单ID不能为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PurchaseOrderId that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}