package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.DomainException;

/**
 * 采购订单ID（包装雪花 Long）。
 */
public record PurchaseOrderId(Long value) {

    public PurchaseOrderId {
        if (value == null || value <= 0) {
            throw new DomainException("采购订单ID非法");
        }
    }
}