package com.company.erp.purchase.domain.valueobject;

/**
 * 入库明细值对象，用作 {@link com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent} 的载荷。
 */
public record ReceivedItem(MaterialId materialId, Quantity quantity, Money unitPrice) {
}