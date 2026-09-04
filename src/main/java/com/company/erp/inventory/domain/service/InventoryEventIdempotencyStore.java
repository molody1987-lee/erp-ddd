package com.company.erp.inventory.domain.service;

/**
 * 库存事件幂等存储端口。
 */
public interface InventoryEventIdempotencyStore {

    boolean markIfAbsent(String eventId);

    void remove(String eventId);
}