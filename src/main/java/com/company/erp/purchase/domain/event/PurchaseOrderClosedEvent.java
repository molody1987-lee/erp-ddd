package com.company.erp.purchase.domain.event;

import lombok.Getter;

/**
 * 采购订单关闭事件。
 */
@Getter
public class PurchaseOrderClosedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderCode;
    private final Long operatorId;
    private final String reason;

    public PurchaseOrderClosedEvent(Long orderId, String orderCode, Long operatorId, String reason) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.operatorId = operatorId;
        this.reason = reason;
    }

    @Override
    public String eventType() {
        return "PurchaseOrderClosedEvent";
    }
}