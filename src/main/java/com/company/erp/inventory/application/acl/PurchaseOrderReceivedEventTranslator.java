package com.company.erp.inventory.application.acl;

import com.company.erp.inventory.domain.valueobject.InventoryQuantity;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 采购入库事件翻译器（防腐层），将跨上下文事件翻译为库存入库条目。
 */
@Component
public class PurchaseOrderReceivedEventTranslator {

    public List<InventoryReceiveItem> translate(PurchaseOrderReceivedEvent event) {
        return event.items().stream()
                .map(item -> new InventoryReceiveItem(
                        item.materialId(),
                        InventoryQuantity.of(item.quantity(), item.unit()),
                        item.unitPrice()))
                .toList();
    }
}