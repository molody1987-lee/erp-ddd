package com.company.erp.inventory.domain.exception;

/**
 * 库存领域异常，用于表达库存业务规则被违反。
 */
public class InventoryDomainException extends RuntimeException {

    public InventoryDomainException(String message) {
        super(message);
    }
}