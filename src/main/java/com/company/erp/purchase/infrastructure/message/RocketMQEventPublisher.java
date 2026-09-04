package com.company.erp.purchase.infrastructure.message;

import com.company.erp.purchase.application.port.DomainEventPublisher;
import com.company.erp.purchase.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 基于 RocketMQ 的领域事件发布实现。
 * 目的地格式：{topic}:{eventType}，消费者按 Tag 精准过滤。
 */
@Service
@RequiredArgsConstructor
public class RocketMQEventPublisher implements DomainEventPublisher {

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.domain-event.topic:erp-domain-event}")
    private String topic;

    @Override
    public void publish(DomainEvent event) {
        String destination = topic + ":" + event.eventType();
        rocketMQTemplate.convertAndSend(destination, event);
    }
}