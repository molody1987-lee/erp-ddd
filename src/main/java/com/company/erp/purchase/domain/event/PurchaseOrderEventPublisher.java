package com.company.erp.purchase.domain.event;

/**
 * 采购领域事件发布端口。
 */
public interface PurchaseOrderEventPublisher {

    void publish(PurchaseOrderReceivedEvent event);
}