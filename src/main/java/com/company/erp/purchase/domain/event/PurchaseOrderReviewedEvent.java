package com.company.erp.purchase.domain.event;

import lombok.Getter;

/**
 * 采购订单审核通过事件。
 */
@Getter
public class PurchaseOrderReviewedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderCode;
    private final Long operatorId;

    public PurchaseOrderReviewedEvent(Long orderId, String orderCode, Long operatorId) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.operatorId = operatorId;
    }

    @Override
    public String eventType() {
        return "PurchaseOrderReviewedEvent";
    }
}