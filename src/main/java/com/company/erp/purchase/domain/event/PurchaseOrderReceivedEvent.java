package com.company.erp.purchase.domain.event;

import com.company.erp.purchase.domain.valueobject.ReceivedItem;
import lombok.Getter;

import java.util.List;

/**
 * 采购入库事件。由库存上下文消费，触发库存增加与移动加权平均成本重算。
 */
@Getter
public class PurchaseOrderReceivedEvent extends DomainEvent {

    private final Long orderId;
    private final Long orgId;
    private final String orderCode;
    private final List<ReceivedItem> items;

    public PurchaseOrderReceivedEvent(Long orderId, Long orgId, String orderCode, List<ReceivedItem> items) {
        this.orderId = orderId;
        this.orgId = orgId;
        this.orderCode = orderCode;
        this.items = items;
    }

    @Override
    public String eventType() {
        return "PurchaseOrderReceivedEvent";
    }
}