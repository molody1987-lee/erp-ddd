package com.company.erp.inventory.application.port;

/**
 * 库存分布式锁端口：在持有锁的情况下执行关键区，作为并发读写同一物料结存的兜底保护。
 */
public interface InventoryDistributedLock {

    /**
     * 在分布式锁保护下执行操作。
     *
     * @param resourceKey 资源键（不含 {@code lock:} 前缀），如 {@code inventory:material_1_100}
     * @param action      需在锁内执行的操作
     */
    void execute(String resourceKey, Runnable action);
}