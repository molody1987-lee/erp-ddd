package com.company.erp.inventory.domain.exception;

/**
 * 库存上下文领域异常。
 */
public class InventoryDomainException extends RuntimeException {

    public InventoryDomainException(String message) {
        super(message);
    }
}