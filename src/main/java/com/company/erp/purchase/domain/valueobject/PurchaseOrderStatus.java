package com.company.erp.purchase.domain.valueobject;

/**
 * 采购订单状态。
 */
public enum PurchaseOrderStatus {

    DRAFT(1, "草稿"),
    REVIEWED(2, "已审核"),
    PARTIAL_RECEIVED(3, "部分入库"),
    RECEIVED(4, "已入库"),
    CLOSED(5, "已关闭");

    private final int code;
    private final String description;

    PurchaseOrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int code() {
        return code;
    }

    public String description() {
        return description;
    }

    public static PurchaseOrderStatus fromCode(int code) {
        for (PurchaseOrderStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的采购订单状态 code: " + code);
    }
}