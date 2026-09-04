package com.company.erp.inventory.application.port;

/**
 * 事件幂等端口：以事件ID（correlationId）去重，防止 RocketMQ 重投导致重复入库。
 */
public interface InventoryEventIdempotencyStore {

    /**
     * 尝试标记该事件为「已处理」。首次返回 true，重复返回 false。
     */
    boolean markIfAbsent(String eventId);

    /**
     * 移除事件标记，用于处理失败时回滚，使 RocketMQ 重投后可重新处理。
     */
    void remove(String eventId);
}