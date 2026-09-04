package com.company.erp.purchase.infrastructure.message;

import com.company.erp.purchase.domain.event.PurchaseOrderEventPublisher;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * 基于 RocketMQ 的采购领域事件发布器。
 */
@Component
@RequiredArgsConstructor
public class RocketMQEventPublisher implements PurchaseOrderEventPublisher {

    public static final String TOPIC = "purchase-order-received-topic";

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void publish(PurchaseOrderReceivedEvent event) {
        rocketMQTemplate.syncSend(TOPIC, event);
    }
}