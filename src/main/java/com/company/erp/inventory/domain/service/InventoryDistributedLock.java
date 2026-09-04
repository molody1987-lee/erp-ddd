package com.company.erp.inventory.domain.service;

/**
 * 库存分布式锁端口。
 */
public interface InventoryDistributedLock {

    void execute(String resourceKey, Runnable action);
}