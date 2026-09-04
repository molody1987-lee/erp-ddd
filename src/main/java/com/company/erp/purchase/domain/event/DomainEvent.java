package com.company.erp.purchase.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类。事件描述「已经发生的事实」，通过 RocketMQ 发布以驱动跨上下文最终一致。
 */
@Getter
public abstract class DomainEvent {

    private final String eventId = UUID.randomUUID().toString();
    private final String sourceContext = "purchase";
    private final LocalDateTime occurredOn = LocalDateTime.now();

    /** 事件类型名，同时作为 RocketMQ 的 Tag 使用。 */
    public abstract String eventType();
}