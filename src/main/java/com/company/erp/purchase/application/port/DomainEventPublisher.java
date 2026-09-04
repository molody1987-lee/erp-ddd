package com.company.erp.purchase.application.port;

import com.company.erp.purchase.domain.event.DomainEvent;

/**
 * 领域事件发布端口，由基础设施层（RocketMQ）实现。
 */
public interface DomainEventPublisher {

    void publish(DomainEvent event);
}