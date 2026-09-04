package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.DomainException;

/**
 * 供应商ID（引用基础数据上下文）。
 */
public record SupplierId(Long value) {

    public SupplierId {
        if (value == null || value <= 0) {
            throw new DomainException("供应商ID非法");
        }
    }
}