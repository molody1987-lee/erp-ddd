package com.company.erp.purchase.domain.exception;

/**
 * 采购上下文领域异常。
 */
public class PurchaseDomainException extends RuntimeException {

    public PurchaseDomainException(String message) {
        super(message);
    }
}