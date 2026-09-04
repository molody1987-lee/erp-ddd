package com.company.erp.inventory.application.listener;

import com.company.erp.inventory.application.acl.PurchaseOrderReceivedEventAcl;
import com.company.erp.inventory.application.service.InventoryReceiveApplicationService;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 消费采购上下文发布的 {@link PurchaseOrderReceivedEvent}，驱动库存入库与移动加权平均成本重算。
 * 通过 Tag（事件类型名）精准过滤，且应用服务保证幂等。
 */
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${rocketmq.domain-event.topic:erp-domain-event}",
        consumerGroup = "erp-inventory-consumer",
        selectorExpression = "PurchaseOrderReceivedEvent")
public class PurchaseOrderReceivedEventListener implements RocketMQListener<PurchaseOrderReceivedEvent> {

    private final PurchaseOrderReceivedEventAcl acl;
    private final InventoryReceiveApplicationService service;

    @Override
    public void onMessage(PurchaseOrderReceivedEvent event) {
        service.receive(acl.toCommands(event));
    }
}