package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.DomainException;

/**
 * 物料ID（引用基础数据上下文）。
 */
public record MaterialId(Long value) {

    public MaterialId {
        if (value == null || value <= 0) {
            throw new DomainException("物料ID非法");
        }
    }
}