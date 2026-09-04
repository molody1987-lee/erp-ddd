package com.company.erp.purchase.domain.event;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * 采购订单创建事件。
 */
@Getter
public class PurchaseOrderCreatedEvent extends DomainEvent {

    private final Long orderId;
    private final String orderCode;
    private final Long supplierId;
    private final Long orgId;
    private final BigDecimal totalAmount;
    private final String currency;

    public PurchaseOrderCreatedEvent(Long orderId, String orderCode, Long supplierId, Long orgId,
                                     BigDecimal totalAmount, String currency) {
        this.orderId = orderId;
        this.orderCode = orderCode;
        this.supplierId = supplierId;
        this.orgId = orgId;
        this.totalAmount = totalAmount;
        this.currency = currency;
    }

    @Override
    public String eventType() {
        return "PurchaseOrderCreatedEvent";
    }
}