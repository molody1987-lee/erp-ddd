package com.company.erp.purchase.domain.valueobject;

import com.company.erp.purchase.domain.exception.DomainException;

/**
 * 组织ID（多组织，引用基础数据上下文）。
 */
public record OrganizationId(Long value) {

    public OrganizationId {
        if (value == null || value <= 0) {
            throw new DomainException("组织ID非法");
        }
    }
}