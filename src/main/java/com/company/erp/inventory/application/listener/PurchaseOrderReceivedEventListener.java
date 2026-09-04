package com.company.erp.inventory.application.listener;

import com.company.erp.inventory.application.service.InventoryAppService;
import com.company.erp.inventory.domain.exception.InventoryDomainException;
import com.company.erp.purchase.domain.event.PurchaseOrderReceivedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 采购入库事件监听器（库存上下文）。
 */
@Component
@RocketMQMessageListener(
        topic = "purchase-order-received-topic",
        consumerGroup = "inventory-purchase-received-group")
@RequiredArgsConstructor
public class PurchaseOrderReceivedEventListener implements RocketMQListener<String> {

    private final ObjectMapper objectMapper;
    private final InventoryAppService inventoryAppService;

    @Override
    public void onMessage(String message) {
        try {
            PurchaseOrderReceivedEvent event =
                    objectMapper.readValue(message, PurchaseOrderReceivedEvent.class);
            inventoryAppService.handlePurchaseReceived(event);
        } catch (JsonProcessingException e) {
            throw new InventoryDomainException("解析采购入库事件失败: " + e.getMessage());
        }
    }
}