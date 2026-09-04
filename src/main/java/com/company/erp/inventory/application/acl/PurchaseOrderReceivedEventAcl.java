package com.company.erp.inventory.application.acl;

import com.company.erp.inventory.application.command.ReceiveInventoryCommand;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.company.erp.purchase.domain.valueobject.ReceivedItem;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 采购入库事件的防腐层（ACL），将采购上下文的领域事件转换为库存上下文自身的命令，
 * 避免库存上下文直接耦合采购上下文的领域模型。
 */
@Component
public class PurchaseOrderReceivedEventAcl {

    public List<ReceiveInventoryCommand> toCommands(PurchaseOrderReceivedEvent event) {
        return event.getItems().stream()
                .map(item -> toCommand(event, item))
                .toList();
    }

    private ReceiveInventoryCommand toCommand(PurchaseOrderReceivedEvent event, ReceivedItem item) {
        return new ReceiveInventoryCommand(
                event.getEventId(),
                event.getOrderId(),
                item.materialId().value(),
                event.getOrgId(),
                item.quantity().value(),
                item.quantity().unit(),
                item.unitPrice().amount(),
                item.unitPrice().currency());
    }
}