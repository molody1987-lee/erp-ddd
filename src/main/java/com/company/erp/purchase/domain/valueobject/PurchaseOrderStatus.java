package com.company.erp.purchase.domain.valueobject;

import lombok.Getter;

/**
 * 采购订单状态。
 */
@Getter
public enum PurchaseOrderStatus {

    DRAFT(0),
    REVIEWED(1),
    RECEIVED(2),
    CLOSED(3);

    private final int code;

    PurchaseOrderStatus(int code) {
        this.code = code;
    }

    public static PurchaseOrderStatus fromCode(int code) {
        for (PurchaseOrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法采购订单状态: " + code);
    }
}