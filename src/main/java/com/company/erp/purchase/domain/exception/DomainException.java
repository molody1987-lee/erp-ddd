package com.company.erp.purchase.domain.exception;

/**
 * 领域异常，用于表达业务规则被违反。
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}