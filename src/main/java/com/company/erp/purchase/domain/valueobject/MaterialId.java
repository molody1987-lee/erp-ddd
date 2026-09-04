package com.company.erp.purchase.domain.valueobject;

import java.util.Objects;

/**
 * 物料ID值对象。
 */
public record MaterialId(Long value) {

    public MaterialId {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("物料ID不能为空");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaterialId that)) {
            return false;
        }
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}