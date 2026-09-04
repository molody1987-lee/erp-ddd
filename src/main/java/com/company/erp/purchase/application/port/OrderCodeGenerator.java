package com.company.erp.purchase.application.port;

/**
 * 采购订单编码生成端口，由基础设施层实现。
 */
public interface OrderCodeGenerator {

    String nextCode();
}